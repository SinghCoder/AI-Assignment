/*-****************************************************************************
 * jni_statematrix.c
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

#include "jni_statematrix.h"
#include "statematrix.h"

#include <stdlib.h>
#include <string.h>

/**
 * Create a new state matrix.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param jfilename Where to store the state matrix.
 * @param num_states The number of states in the matrix.
 * @param num_actions The number of actions per state.
 * @return the descriptor to use when referencing the created matrix.
 */
JNIEXPORT jint JNICALL Java_dots_agent_q_NativeStateMatrix_c_1create
  (JNIEnv *env, jclass class, jbyteArray jfilename, jlong num_states,
   jint num_actions)
{
    char *filename;
    jint descriptor, flength;
    jboolean is_copy;
    jbyte *_filename;

    flength = (*env)->GetArrayLength(env, jfilename);

    if ((filename = calloc(sizeof(char), flength + 1)) == NULL)
        return -1;

    /* Fetch byte array. */
    _filename = (*env)->GetByteArrayElements(env, jfilename, &is_copy);
    strncpy(filename, (const char *)_filename, flength);

    /* Release the byte array. */
    (*env)->ReleaseByteArrayElements(env, jfilename, _filename, 0);

    descriptor = statematrix_create(filename,
            num_states, num_actions);

    free(filename);

    return descriptor;
}

/**
 * Get a Q value from a state matrix.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to a state matrix.
 * @param state The state part of the Q value.
 * @param action The action part of the Q value.
 * @return the Q value of the given state and action pair in the
 * referenced matrix.
 */
JNIEXPORT jfloat JNICALL Java_dots_agent_q_NativeStateMatrix_c_1getQ
  (JNIEnv *env, jclass class, jint fd, jlong state, jint action)
{
    return statematrix_get_q(fd, state, action);
}

/**
 * Set a Q value in a state matrix.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to a state matrix.
 * @param state The state part of the Q value.
 * @param action The action part of the Q value.
 * @param value The Q value.
 */
JNIEXPORT void JNICALL Java_dots_agent_q_NativeStateMatrix_c_1setQ
  (JNIEnv *env, jclass class, jint fd, jlong state, jint action, jfloat value)
{
    statematrix_set_q(fd, state, action, value);
}

/**
 * Find the maximum Q value for a given state.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to a state matrix.
 * @param state The state in question.
 * @return the maxiumum Q value for all possible actions for the given state.
 */
JNIEXPORT jfloat JNICALL Java_dots_agent_q_NativeStateMatrix_c_1getMaxValue
  (JNIEnv *env, jclass class, jint fd, jlong state)
{
    return statematrix_get_max_q(fd, state);
}

/**
 * Load a state matrix from file.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param jfilename The filename of the state matrix to open.
 * @return the descriptor to use when referencing the created matrix.
 */
JNIEXPORT jint JNICALL Java_dots_agent_q_NativeStateMatrix_c_1open
  (JNIEnv *env, jclass class, jbyteArray jfilename)
{
    char *filename;
    jint descriptor, flength;
    jbyte *_filename;
    jboolean is_copy;

    flength = (*env)->GetArrayLength(env, jfilename);

    if ((filename = calloc(sizeof(char), flength + 1)) == NULL)
        return -1;

    /* Fetch byte array. */
    _filename = (*env)->GetByteArrayElements(env, jfilename, &is_copy);
    strncpy(filename, (const char *)_filename, flength);

    /* Release byte array. */
    (*env)->ReleaseByteArrayElements(env, jfilename, _filename, 0);

    descriptor = statematrix_load(filename);

    free(filename);

    return descriptor;
}

/**
 * Retrieve the number of actions in a given matrix.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to a state matrix.
 * @return the number of actions in the matrix referred.
 */
JNIEXPORT jint JNICALL Java_dots_agent_q_NativeStateMatrix_c_1getNumActions
  (JNIEnv *env, jclass class, jint fd)
{
    return statematrix_get_num_actions(fd);
}

/**
 * Retrieve the number of states in a given matrix.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to a state matrix.
 * @return the number of states in the matrix referred.
 */
JNIEXPORT jlong JNICALL Java_dots_agent_q_NativeStateMatrix_c_1getNumStates
  (JNIEnv *env, jclass class, jint fd)
{
    return statematrix_get_num_states(fd);
}

/**
 * Save a state matrix to file.
 *
 * @param env The Java environment.
 * @param class The Java class this function belongs to.
 * @param fd Reference to the state matrix that should be saved.
 */
JNIEXPORT jint JNICALL Java_dots_agent_q_NativeStateMatrix_c_1save
  (JNIEnv *env, jclass class, jint fd)
{
    return statematrix_save(fd);
}
