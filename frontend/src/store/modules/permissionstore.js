/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  permissions: {
    value: [],
    loading: false,
    error: undefined,
    request: undefined,
  },
  permission: {
    value: null,
    loading: false,
  },
};

export const mutations = {
  ADD_PERMISSION(state, permission) {
    const index = state.permissions.value.findIndex((_) => _.id === permission.id);
    if (index === -1) {
      // if permission does not exist add it
      state.permissions.value.push(permission);
    } else {
      // else update the permission
      state.permissions.value[index] = permission;
    }
    state.permission.value = permission;
  },
  DELETE_PERMISSION(state, permission) {
    state.permissions.value = state.permissions.value.filter((auth) => auth.id !== permission.id);
    state.permission = { value: null, loading: false };
  },
  UPDATE_PERMISSION(state, permission) {
    state.permissions.value.forEach((auth, i) => {
      if (auth.id === permission.id) state.permissions.value[i] = permission;
    });
  },
  CLEAR_PERMISSIONS(state) {
    state.permissions.value = [];
    state.permissions.error = undefined;
  },
  INCREMENT_PERMISSIONS_REQ_ID(state) {
    state.permissions.requestId += 1;
  },
  SET_LOADING_PERMISSIONS(state, val) {
    state.permissions.loading = val;
  },
  ADD_REQUEST_PERMISSION(state, val) {
    if (state.permissions.request) {
      state.permissions.request.cancel('abort');
    }
    state.permissions.request = val;
  },
};

export const actions = {
  fetch_permission({ commit, rootGetters }, id) {
    axios.get(`${rootGetters.endpoint('permissions')}/${id}`)
      .then(({ data }) => commit('ADD_PERMISSION', decodeObject(data, encodedFields.permission)))
      .catch(console.error);
  },
  fetch_permissions_pagination({ commit, rootGetters }, paginationParams) {
    commit('SET_LOADING_PERMISSIONS', true);
    commit('CLEAR_PERMISSIONS');
    return axios.get(rootGetters.endpoint('permissions'), {
      params: {
        ...paginationParams,
      },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.permissionList.forEach(
            (permission) => commit('ADD_PERMISSION', decodeObject(permission, encodedFields.permission)),
          );
        }
        commit('SET_LOADING_PERMISSIONS', false);
        return data.total;
      })
      .catch((e) => {
        if (e.message !== 'abort') {
          console.error(e);
        }
        return 0;
      });
  },
  create_permission({ commit, rootGetters }, permission) {
    encodeObject(permission, encodedFields.permission);
    axios.post(rootGetters.endpoint('permissions'), permission)
      .then(({ data }) => commit('ADD_PERMISSION', decodeObject(data, encodedFields.permission)))
      .catch(console.error);
  },
  update_permission({ commit }, permission) {
    encodeObject(permission, encodedFields.permission);
    axios.patch(permission._links.self.href, permission)
      .then(() => commit('UPDATE_PERMISSION', decodeObject(permission, encodedFields.permission)))
      .catch(console.error);
  },
  delete_permission({ commit }, permission) {
    axios.delete(permission._links.self.href)
      .then(() => commit('DELETE_PERMISSION', permission))
      .catch(console.error);
  },
  upload_permission_file({ rootGetters }, formData) {
    return axios.post(rootGetters.endpoint('files'), formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};
