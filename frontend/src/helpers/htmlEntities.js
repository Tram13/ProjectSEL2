/* eslint no-param-reassign: 0 */
import { encode, decode } from 'html-entities';

const encodedFields = {
  user: ['firstName', 'lastName'],
  cert: ['name'],
  contact: ['firstName', 'lastName'],
  organisation: ['name', 'serviceProvider'],
  service: ['name', 'domain', 'description'],
  package: ['name'],
  permission: ['name', 'description'],
  proposal: [
    'name',
    'businessContext',
    'legalContext',
    'functionalSetup',
    'technicalSetup',
    'purposeRequestedData',
    'originalLegalContext',
    'ftpAccount',
    'architectureVisualizationExplanation',
  ],
};

function convertEntity(obj, fields, func) {
  fields.forEach((field) => {
    if (obj[field]) {
      obj[field] = func(obj[field], { mode: 'nonAsciiPrintable' });
    }
  });
  return obj;
}

function encodeObject(obj, fields) {
  return convertEntity(obj, fields, encode);
}

function decodeObject(obj, fields) {
  return convertEntity(obj, fields, decode);
}

export {
  encodeObject,
  decodeObject,
  encodedFields,
};
