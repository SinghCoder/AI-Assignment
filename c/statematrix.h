/*-****************************************************************************
 * statematrix.h
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

/* Default Q value when creating new state matrices. */
#define DEFAULT_Q_VALUE (0)

/**
 * Create a new state matrix.
 *
 * The return value will be >= 0 if successful. A return value of
 * -1 indicates that the creation of the state matrix failed due to memory
 * limitations.
 *
 * @param filename The filename of the file where to save the matrix.
 * @param num_states The number of states in the matrix to create.
 * @param num_actions The number of actions per state.
 * @return a descriptor to use when referencing the created matrix.
 */
int statematrix_create(const char *filename, long num_states, int num_actions);

/**
 * Load a state matrix from file.
 *
 * The return value will be >= 0 if successful. A return value of
 * -1 indicates that the creation of the state matrix failed due to memory
 * limitations.
 *
 * @param filename The filename of the file to load.
 * @return a descriptor to use when referencing the created matrix.
 */
int statematrix_load(const char *filename);

/**
 * Save a state matrix to file.
 *
 * Will return -1 iff not successful.
 *
 * @param descriptor A reference to the state matrix to save.
 * @return 0 iff successful.
 */
int statematrix_save(int descriptor);

/**
 * Retrieve a Q value from the state matrix referenced.
 *
 * @param descriptor A reference to a state matrix.
 * @param state The state part of the state-action-pair.
 * @param action The action part of the state-action-pair.
 * @return The Q value of the state-action-pair.
 */
float statematrix_get_q(int descriptor, long state, int action);

/**
 * Set a Q value in the state matrix referenced.
 *
 * @param descriptor A reference to the state matrix in question.
 * @param state The state part of the state-action-pair.
 * @param action The action part of the state-action-pair.
 * @param value The new Q value of the state-action-pair.
 */
void statematrix_set_q(int descriptor, long state, int action, float value);

/**
 * Retrieve the maximum value for the given state in the referenced state
 * matrix.
 *
 * @param descriptor A reference to the state matrix in question.
 * @param state The state in question.
 * @return the maximum Q value for all state-action-pairs in the specified
 * state.
 */
float statematrix_get_max_q(int descriptor, long state);

/**
 * Retrieve the number of states for the referenced state matrix.
 *
 * Will return -1 iff not successful.
 *
 * @param descriptor A reference to the state matrix in question.
 * @return the number of states in the state matrix.
 */
long statematrix_get_num_states(int descriptor);

/**
 * Retrieve the number of actions per state for the referenced state matrix.
 *
 * Will return -1 iff not successful.
 *
 * @param descriptor A reference to the state matrix in question.
 * @return the number of actions per state in the state matrix.
 */
int statematrix_get_num_actions(int descriptor);
