/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

function postFile(formData, rootGetters) {
  return axios.post(rootGetters.endpoint('files'), formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
}

export const state = {
  // list of certs
  certs: {
    value: [],
    loading: false,
    error: undefined,
    request: undefined,
  },
  // single cert if for inspect pages etc
  cert: {
    value: null,
    organisations: [],
  },
  uploadingDone: false,
  data: undefined,
  organisation: { organisationName: '' },
};

export const mutations = {
  SET_CERT(state, cert) {
    // set cert
    state.cert.value = cert;
  },
  ADD_CERT(state, cert) {
    const index = state.certs.value.findIndex((c) => c.id === cert.id);
    if (index === -1) {
      // if cert does not exist add it
      state.certs.value.push(cert);
    } else {
      // else update the cert
      state.certs.value[index] = cert;
    }
  },
  DELETE_CERT(state, cert) {
    state.certs.value = state.certs.value.filter((c) => c.id !== cert.id);
  },
  UPDATE_CERT(state, cert) {
    state.certs.value.forEach((c, i) => {
      if (c.id === cert.id) state.certs.value[i] = cert;
    });
    if (state.cert.value.id === cert.id) state.cert.value = cert;
  },
  CLEAR_CERTS(state) {
    state.certs.value = [];
    state.certs.error = undefined;
  },
  TOGGLE_LOADING_CERT(state) {
    state.cert.loading = !state.cert.loading;
  },
  SET_LOADING_CERTS(state, val) {
    state.certs.loading = val;
  },
  ADD_REQUEST_CERT(state, val) {
    if (state.certs.request) {
      state.certs.request.cancel('abort');
    }
    state.certs.request = val;
  },
  UPLOAD_RESET(state) {
    state.uploadingDone = false;
    state.data = undefined;
  },
  UPLOAD_DONE(state, data) {
    state.uploadingDone = true;
    state.data = data;
  },
  CERT_ORG(state, data) {
    state.organisation = data;
  },
};

export const actions = {
  get_cert({
    commit,
    rootGetters,
  }, id) {
    commit('TOGGLE_LOADING_CERT');
    return axios.get(`${rootGetters.endpoint('certificates')}/${id}`)
      .then(({ data }) => commit('SET_CERT', decodeObject(data, encodedFields.cert)))
      .catch(console.error)
      .finally(() => commit('TOGGLE_LOADING_CERT'));
  },
  get_certs_pagination({
    commit,
    rootGetters,
  }, paginationParams, filterParams) {
    commit('SET_LOADING_CERTS', true);
    commit('CLEAR_CERTS');
    const cancelToken = axios.CancelToken;
    const source = cancelToken.source();
    commit('ADD_REQUEST_CERT', source);
    return axios.get(rootGetters.endpoint('certificates'), {
      params: {
        ...paginationParams,
        ...filterParams,
      },
      cancelToken: source.token,
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.certificateList.forEach((cert) => commit('ADD_CERT', decodeObject(cert, encodedFields.cert)));
        }
        return data.total;
      })
      .catch((e) => {
        if (e.message !== 'abort') {
          console.error(e);
        }
        return 0;
      })
      .finally(() => commit('SET_LOADING_CERTS', false));
  },
  reset_upload({ commit }) {
    commit('UPLOAD_RESET');
  },
  create_cert({
    commit,
    rootGetters,
  }, { file, cert }) {
    return postFile(file, rootGetters).then((res) => {
      // update cert post data with file id
      cert.file = res.data.id;
      axios.post(rootGetters.endpoint('certificates'), cert)
        .then(({ data }) => commit('UPLOAD_DONE', data)).catch(console.error);
    }).catch(console.error);
  },
  delete_cert({ commit }, cert) {
    axios.delete(cert._links.self.href)
      .then(() => commit('DELETE_CERT', cert))
      .catch(console.error);
  },
  generating({ commit }, bool) {
    commit('GENERATING', bool);
  },
  get_org_cert({ commit }, link) {
    axios.get(link)
      .then((data) => commit('CERT_ORG', data.data))
      .catch(console.error);
  },
};
