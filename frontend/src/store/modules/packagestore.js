/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  // list of packages
  packages: {
    value: [],
    loading: false,
  },
  // single package if for inspect pages etc
  package: { value: null, loading: false, error: undefined },
  packageServices: { loading: false },
};

export const mutations = {
  SET_PACKAGE(state, pack) {
    state.package.value = pack;
    state.package.value.services = [];
  },
  ADD_PACKAGE(state, pack) {
    const index = state.packages.value.findIndex((_) => _.id === pack.id);
    if (index === -1) {
      // if package does not exist add it
      state.packages.value.push(pack);
    } else {
      // else update the package
      state.packages.value[index] = pack;
    }
  },
  ADD_PACKAGE_SERVICE(state, { service, source, deliveryMethod }) {
    const index = state.package.value.services.findIndex((_) => _.id === service.id);
    if (index === -1) {
      state.package.value.services.push({ ...service, source, deliveryMethod });
    } else {
      state.package.value.services[index] = { ...service, source, deliveryMethod };
    }
  },
  DELETE_PACKAGE(state, pack) {
    state.package = { value: null, loading: false, error: undefined };
    if (state.packages.value) {
      state.packages.value = state.packages.value.filter((p) => p.id !== pack.id);
    }
  },
  UPDATE_PACKAGE(state, pack) {
    state.packages.value.forEach((p, i) => {
      if (p.id === pack.id) state.packages.value[i] = pack;
    });
    if (state.package.value.id === pack.id) {
      state.package.value = pack;
      state.package.value.services = [];
    }
  },
  CLEAR_PACKAGES(state) {
    state.packages.value = [];
  },
  CLEAR_PACKAGE(state) {
    state.package = { value: null, loading: false, error: undefined };
  },
  CLEAR_PACKAGE_SERVICES(state) {
    state.package.value.services = [];
  },
  SET_PACKAGE_SERVICE(state, updatedService) {
    const copy = updatedService;
    const index = state.package.value.services.findIndex(
      (service) => service.href === copy._links.self.href,
    );
    copy.serviceId = updatedService.id;
    delete copy.id;
    if (index !== -1) {
      state.package.value.services[index] = {
        ...state.package.value.services[index],
        ...copy,
      };
    } else {
      state.package.value.services.push(copy);
    }
  },
  TOGGLE_PACKAGES_LOADING(state) {
    state.packages.loading = !state.packages.loading;
  },
  TOGGLE_PACKAGE_LOADING(state) {
    state.package.loading = !state.package.loading;
  },
  TOGGLE_PACKAGE_SERVICES_LOADING(state) {
    state.packageServices.loading = !state.packageServices.loading;
  },
};

export const actions = {
  fetch_package({ commit, rootGetters, dispatch }, id) {
    commit('CLEAR_PACKAGE');
    commit('TOGGLE_PACKAGE_LOADING');
    return axios.get(`${rootGetters.endpoint('packages')}/${id}`)
      .then(({ data }) => commit('SET_PACKAGE', decodeObject(data, encodedFields.package)))
      .then(() => dispatch('fetch_package_services', id))
      .catch(console.error)
      .finally(() => commit('TOGGLE_PACKAGE_LOADING'));
  },
  fetch_packages_pagination({ commit, rootGetters }, paginationParams) {
    commit('CLEAR_PACKAGES');
    commit('TOGGLE_PACKAGES_LOADING');
    return axios.get(rootGetters.endpoint('packages'), {
      params: { ...paginationParams },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.packageList.forEach(
            (pack) => commit('ADD_PACKAGE', decodeObject(pack, encodedFields.package)),
          );
        }
        return data.total;
      })
      .catch(console.error)
      .finally(() => commit('TOGGLE_PACKAGES_LOADING'));
  },
  fetch_package_services({ commit, rootGetters }, id) {
    commit('CLEAR_PACKAGE_SERVICES');
    return axios.get(`${rootGetters.endpoint('packages')}/${id}/services`)
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.packageServiceList.forEach(
            (service) => commit('ADD_PACKAGE_SERVICE', {
              service: decodeObject(service.service, encodedFields.service),
              source: service.source,
              deliveryMethod: service.deliveryMethod,
            }),
          );
        }
      })
      .catch(console.error);
  },
  fetch_package_services_pagination({ commit, rootGetters }, paginationParams) {
    commit('TOGGLE_PACKAGE_SERVICES_LOADING');
    commit('CLEAR_PACKAGE_SERVICES');
    const id = paginationParams.packageId;
    // eslint-disable-next-line no-param-reassign
    delete paginationParams.packageId;
    return axios.get(`${rootGetters.endpoint('packages')}/${id}/services`, {
      params: { ...paginationParams },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.packageServiceList.forEach(
            (service) => commit('ADD_PACKAGE_SERVICE', {
              service: decodeObject(service.service, encodedFields.service),
              source: service.source,
              deliveryMethod: service.deliveryMethod,
            }),
          );
        }
        return data.total;
      })
      .catch(console.error)
      .finally(() => commit('TOGGLE_PACKAGE_SERVICES_LOADING'));
  },
  create_package({ commit, rootGetters }, pack) {
    encodeObject(pack, encodedFields.package);
    axios.post(rootGetters.endpoint('packages'), pack)
      .then(({ data }) => commit('ADD_PACKAGE', decodeObject(data, encodedFields.package)))
      .catch(console.error);
  },
  update_package({ commit, dispatch }, pack) {
    encodeObject(pack, encodedFields.package);
    return axios.patch(pack._links.self.href, pack)
      .then(({ data }) => commit('UPDATE_PACKAGE', decodeObject(data, encodedFields.package)))
      .then(() => dispatch('fetch_package_services', pack.id))
      .catch(console.error);
  },
  delete_package({ commit }, pack) {
    axios.delete(pack._links.self.href)
      .then(() => commit('DELETE_PACKAGE', pack))
      .catch(console.error);
  },
};
