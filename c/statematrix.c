/*-****************************************************************************
 * statematrix.c
 ******************************************************************************
 * Copyright (C) 2010 Oskar Arvidsson, Linus Wallgren
 *
 * This file is part of dotsnboxes.
 *
 * dotsnboxes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * dotsnboxes is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * dotsnboxes. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

#include "statematrix.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

typedef struct _stateFile StateFile;

/**
 * For storing meta information about a state file.
 */
struct _stateFile {
    char *filename;                 /* The filename where to save matrix. */
    int num_states;                 /* The number of states in the matrix. */
    int num_actions;                /* The number of actions per state. */
    float *matrix;                  /* The actual matrix. */
    StateFile *next;                /* The next state file in the list. */
};

static StateFile *fileRoot = NULL;  /* List of loaded files. */

/**
 * Append the given file to the list of loaded files.
 *
 * @param file The StateFile to append.
 * @return a reference to the StateFile.
 */
static int statematrix_append(StateFile *file);

/**
 * Retrieve a StateFile from the list of loaded files.
 *
 * @param descriptor The reference to the StateFile.
 * @return a StateFile pointer if successful, NULL otherwise.
 */
static StateFile *statematrix_get(int descriptor);

int statematrix_create(const char *_filename, long num_states, int num_actions)
{
    int i;
    float *matrix;
    char *filename;
    StateFile *file;

    file = malloc(sizeof(StateFile));
    matrix = malloc(sizeof(float) * num_states * num_actions);
    filename = malloc(sizeof(char) * strlen(_filename) + 1);

    if (file == NULL || matrix == NULL || filename == NULL) {
        free(file);
        free(matrix);
        free(filename);
        return -1;
    }

    strcpy(filename, _filename);

    for (i = 0; i < num_states * num_actions; ++i)
        matrix[i] = DEFAULT_Q_VALUE;

    file->filename      = filename;
    file->matrix        = matrix;
    file->num_states    = num_states;
    file->num_actions   = num_actions;
    file->next          = NULL;

    return statematrix_append(file);
}

int statematrix_load(const char *_filename)
{
    StateFile *file;
    float *matrix;
    FILE *fp;
    char *filename;

    file = malloc(sizeof(StateFile));
    filename = malloc(sizeof(char) * strlen(_filename) + 1);

    if (file == NULL || filename == NULL) {
        free(file);
        free(filename);
        return -1;
    }

    strcpy(filename, _filename);

    /* Open file for reading and read meta data. */
    if ((fp = fopen(filename, "rb")) == NULL
            || fread(file, sizeof(StateFile), 1, fp) != 1) {
        free(file);
        free(filename);

        if (fp != NULL)
            fclose(fp);
        return -1;
    }

    /* Create and read matrix. */
    if ((matrix = malloc(sizeof(float) *
                    file->num_states * file->num_actions)) == NULL
            || fread(matrix, sizeof(float),
                file->num_states * file->num_actions, fp)
            != file->num_states * file->num_actions) {
        free(file);
        free(matrix);
        free(filename);
        fclose(fp);
        return -1;
    }

    fclose(fp);

    file->matrix = matrix;
    file->filename = filename;
    file->next = NULL;

    return statematrix_append(file);
}

int statematrix_save(int descriptor)
{
    FILE *fp;
    StateFile *file;

    if ((file = statematrix_get(descriptor)) == NULL)
        return -1;

    if ((fp = fopen(file->filename, "wb")) == NULL)
        return -1;

    if (fwrite(file, sizeof(StateFile), 1, fp) != 1
            || fwrite(file->matrix, sizeof(float),
                file->num_states * file->num_actions, fp)
            != file->num_states * file->num_actions) {
        fclose(fp);
        return -1;
    }

    fclose(fp);

    return 0;
}

float statematrix_get_q(int descriptor, long state, int action)
{
    StateFile *file;

    return ((file = statematrix_get(descriptor)) == NULL) ?
        -1 : file->matrix[state * file->num_actions + action];
}

void statematrix_set_q(int descriptor, long state, int action, float value)
{
    StateFile *file;

    if ((file = statematrix_get(descriptor)) != NULL)
        file->matrix[state * file->num_actions + action] = value;
}

float statematrix_get_max_q(int descriptor, long state)
{
    float max, *current;
    int num_actions;
    StateFile *file;

    if ((file = statematrix_get(descriptor)) == NULL)
        return -1;

    num_actions = file->num_actions;
    current = &file->matrix[state * num_actions + 0];
    max = *current;

    while (num_actions > 0) {
        ++current;
        --num_actions;
        if (*current > max)
            max = *current;
    }

    return max;
}

long statematrix_get_num_states(int descriptor)
{
    StateFile *file;

    return ((file = statematrix_get(descriptor)) == NULL) ?
        -1 : file->num_states;
}

int statematrix_get_num_actions(int descriptor)
{
    StateFile *file;

    return ((file = statematrix_get(descriptor)) == NULL) ?
        -1 : file->num_actions;
}

int statematrix_append(StateFile *file)
{
    int descriptor;
    StateFile **nextp;

    descriptor = 0;

    nextp = &fileRoot;
    while (*nextp != NULL) {
        nextp = &(*nextp)->next;
        ++descriptor;
    }

    *nextp = file;

    return descriptor;
}

StateFile *statematrix_get(int descriptor)
{
    StateFile *file;
    file = fileRoot;

    if (descriptor < 0)
        return NULL;

    while (descriptor > 0 && file != NULL) {
        file = file->next;
        --descriptor;
    }

    return file;
}
