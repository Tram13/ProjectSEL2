/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  services: {
    value: [],
    loading: false,
    error: undefined,
    request: undefined,
  },
  service: {
    value: null,
    loading: false,
  },
};

export const mutations = {
  SET_SERVICE(state, service) {
    state.service.value = service;
  },
  ADD_SERVICE(state, service) {
    const index = state.services.value.findIndex((_) => _.id === service.id);
    if (index === -1) {
      // if service does not exist add it
      state.services.value.push(service);
    } else {
      // else update the service
      state.services.value[index] = service;
    }
  },
  DELETE_SERVICE(state, service) {
    state.services.value = state.services.value.filter((serv) => serv.id !== service.id);
  },
  UPDATE_SERVICE(state, service) {
    state.services.value.forEach((serv, i) => {
      if (serv.id === service.id) state.services[i] = service;
    });
  },
  CLEAR_SERVICES(state) {
    state.services.value = [];
  },
  TOGGLE_SERVICE_LOADING(state) {
    state.service.loading = !state.service.loading;
  },
  SET_SERVICES_LOADING(state, val) {
    state.services.loading = val;
  },
  INCREMENT_SERVICES_REQ_ID(state) {
    state.services.value.requestId += 1;
  },
  ADD_REQUEST_SERVICE(state, val) {
    if (state.services.request) {
      state.services.request.cancel('abort');
    }
    state.services.request = val;
  },
};

export const actions = {
  fetch_service({ commit, rootGetters }, id) {
    commit('TOGGLE_SERVICE_LOADING');
    axios.get(`${rootGetters.endpoint('services')}/${id}`)
      .then(({ data }) => commit('SET_SERVICE', decodeObject(data, encodedFields.service)))
      .catch(console.error)
      .finally(() => commit('TOGGLE_SERVICE_LOADING'));
  },
  fetch_services({ commit, rootGetters }) {
    commit('TOGGLE_SERVICE_LOADING');
    axios.get(rootGetters.endpoint('services'))
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.serviceList.forEach(
            (service) => commit('ADD_SERVICE', decodeObject(service, encodedFields.service)),
          );
        }
        commit('TOGGLE_SERVICE_LOADING');
      })
      .catch(console.error);
  },
  fetch_services_pagination({ commit, rootGetters }, paginationParams) {
    commit('SET_SERVICES_LOADING', true);
    commit('CLEAR_SERVICES');
    commit('INCREMENT_SERVICES_REQ_ID');
    const cancelToken = axios.CancelToken;
    const source = cancelToken.source();
    commit('ADD_REQUEST_SERVICE', source);
    return axios.get(rootGetters.endpoint('services'), {
      params: {
        ...paginationParams,
      },
      cancelToken: source.token,
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.serviceList.forEach(
            (service) => commit('ADD_SERVICE', decodeObject(service, encodedFields.service)),
          );
        }
        return data.total;
      })
      .catch((e) => {
        if (e.message !== 'abort') {
          console.error(e);
        }
        return 0;
      })
      .finally(() => commit('SET_SERVICES_LOADING', false));
  },
  create_service({ commit, rootGetters }, service) {
    encodeObject(service, encodedFields.service);
    axios.post(rootGetters.endpoint('services'), service)
      .then(({ data }) => commit('SET_SERVICE', decodeObject(data, encodedFields.service)))
      .catch(console.error);
  },
  update_service({ commit }, service) {
    encodeObject(service, encodedFields.service);
    axios.patch(service._links.self.href, service)
      .then(() => commit('SET_SERVICE', decodeObject(service, encodedFields.service)))
      .catch(console.error);
  },
  delete_service({ commit }, service) {
    axios.delete(service._links.self.href)
      .then(() => commit('DELETE_SERVICE', service))
      .catch(console.error);
  },
};
