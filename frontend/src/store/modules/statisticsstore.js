/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */

const axios = require('axios');

export const state = {
  statsEndpoints: { value: null, loading: false, error: undefined },
  usersStats: { value: null, loading: false, error: undefined },
  organisationsStats: { value: null, loading: false, error: undefined },
  proposalsStats: { value: null, loading: false, error: undefined },
  servicesStats: { value: null, loading: false, error: undefined },
  packagesStats: { value: null, loading: false, error: undefined },
};

export const mutations = {
  SET_STATISTICS_ENDPOINTS(state, endpoints) {
    state.statsEndpoints.value = endpoints;
  },
  SET_STATISTICS_LOADING(state, loading) {
    state.statsEndpoints.loading = loading;
  },

  SET_USERS_STATS(state, stats) {
    state.usersStats.value = stats;
  },
  SET_USERS_LOADING(state, loading) {
    state.usersStats.loading = loading;
  },

  SET_ORGANISATIONS_STATS(state, stats) {
    state.organisationsStats.value = stats;
  },
  SET_ORGANISATIONS_LOADING(state, loading) {
    state.organisationsStats.loading = loading;
  },

  SET_PROPOSAlS_STATS(state, stats) {
    state.proposalsStats.value = stats;
  },
  SET_PROPOSAlS_LOADING(state, loading) {
    state.proposalsStats.loading = loading;
  },

  SET_SERVICES_STATS(state, stats) {
    state.servicesStats.value = stats;
  },
  SET_SERVICES_LOADING(state, loading) {
    state.servicesStats.loading = loading;
  },

  SET_PACKAGES_STATS(state, stats) {
    state.packagesStats.value = stats;
  },
  SET_PACKAGES_LOADING(state, loading) {
    state.packagesStats.loading = loading;
  },
};

export const actions = {
  // fetches the links for specific statistics
  async fetch_stats_endpoints({ commit, rootGetters }) {
    commit('SET_STATISTICS_LOADING', true);
    return axios.get(rootGetters.endpoint('statistics'))
      .then(({ data }) => commit('SET_STATISTICS_ENDPOINTS', data))
      .finally(() => commit('SET_STATISTICS_LOADING', false));
  },

  fetch_users_stats({ commit }) {
    commit('SET_USERS_LOADING', true);
    axios.get(state.statsEndpoints.value._links.users.href)
      .then(({ data }) => commit('SET_USERS_STATS', data))
      .catch(console.error)
      .finally(() => commit('SET_USERS_LOADING', false));
  },

  fetch_organisations_stats({ commit }) {
    commit('SET_ORGANISATIONS_LOADING', true);
    axios.get(state.statsEndpoints.value._links.organisations.href)
      .then(({ data }) => commit('SET_ORGANISATIONS_STATS', data))
      .catch(console.error)
      .finally(() => commit('SET_ORGANISATIONS_LOADING', false));
  },

  fetch_proposals_stats({ commit }) {
    commit('SET_PROPOSAlS_LOADING', true);
    axios.get(state.statsEndpoints.value._links.proposals.href)
      .then(({ data }) => commit('SET_PROPOSAlS_STATS', data))
      .catch(console.error)
      .finally(() => commit('SET_PROPOSAlS_LOADING', false));
  },

  fetch_services_stats({ commit }) {
    commit('SET_SERVICES_LOADING', true);
    axios.get(state.statsEndpoints.value._links.services.href)
      .then(({ data }) => commit('SET_SERVICES_STATS', data))
      .catch(console.error)
      .finally(() => commit('SET_SERVICES_LOADING', false));
  },

  fetch_packages_stats({ commit }) {
    commit('SET_PACKAGES_LOADING', true);
    axios.get(state.statsEndpoints.value._links.packages.href)
      .then(({ data }) => commit('SET_PACKAGES_STATS', data))
      .catch(console.error)
      .finally(() => commit('SET_PACKAGES_LOADING', false));
  },
};
