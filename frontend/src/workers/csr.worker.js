const forge = require('node-forge');

// eslint-disable-next-line import/prefer-default-export
export const genCSR = async (state) => {
  const { pki } = forge;
  const keys = pki.rsa.generateKeyPair(2048);
  const csr = pki.createCertificationRequest();
  csr.publicKey = keys.publicKey;
  csr.setSubject([{
    name: 'commonName',
    value: state.CN,
  }, {
    name: 'countryName',
    value: state.Land,
  }, {
    shortName: 'ST',
    value: state.Provincie,
  }, {
    name: 'localityName',
    value: state.Stad,
  }, {
    name: 'organizationName',
    value: state.Organisation,
  }, {
    shortName: 'OU',
    value: state.Organisatieunit,
  }]);
  csr.sign(keys.privateKey);
  return [
    pki.privateKeyToPem(keys.privateKey),
    pki.publicKeyToPem(keys.publicKey),
    pki.certificationRequestToPem(csr),
  ];
};
