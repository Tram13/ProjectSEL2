/* eslint no-underscore-dangle: 0 */
import { createStore } from 'vuex';
import * as userstore from '@/store/modules/userstore';
import * as organisationstore from '@/store/modules/organisationstore';
import * as contactstore from '@/store/modules/contactstore';
import * as proposalstore from '@/store/modules/proposalstore';
import * as servicestore from '@/store/modules/servicestore';
import * as permissionstore from '@/store/modules/permissionstore';
import * as authenticationstore from '@/store/modules/authenticationstore';
import * as packagestore from '@/store/modules/packagestore';
import * as certstore from '@/store/modules/certstore';
import * as statisticsstore from '@/store/modules/statisticsstore';
import axios from 'axios';

export default createStore({
  state: {
    endpoints: null,
  },
  mutations: {
    SET_ENDPOINTS(state, endpoints) {
      state.endpoints = endpoints._links;
    },
  },
  actions: {
    get_endpoints({ commit }) {
      const baseUrl = 'https://sel2-2.ugent.be/api/dev/';
      return axios.get(baseUrl)
        .then(({ data }) => commit('SET_ENDPOINTS', data))
        .catch(() => console.log('Could not get endpoints from api.'));
    },
  },
  getters: {
    endpoint: (state) => (endpoint) => state.endpoints[endpoint].href,
  },
  modules: {
    userstore,
    organisationstore,
    contactstore,
    proposalstore,
    servicestore,
    permissionstore,
    authenticationstore,
    packagestore,
    certstore,
    statisticsstore,
  },
});
