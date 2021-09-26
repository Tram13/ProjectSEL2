const kboRegex = /^[01]\d{9}$|^[01]\d{3}\.\d{3}\.\d{3}$/;
const ovoRegex = /^OVO\d{6}$/;
const nisRegex = /^\d{5}$/;
const dateFormat = /^(\d{2}(-|\/)){2}\d{2}(\d{2})?$/;

const phoneRegex = new RegExp([
  '^',
  '(',
  '(', // Belgium
  '((\\+|00)(32)(\\s|-)?|0)',
  '(',
  '((\\d(\\s|-)?\\d{3}|\\d{2}(\\s|-)?\\d{2})((\\s|-)?\\d{2}){2})',
  '|(4(60|[789]\\d)((\\s|-)?\\d{2}){3})', // mobile
  ')',
  ')',
  '|',
  '(', // the Netherlands
  '((\\+|00)(31)(\\s|-)?|0)',
  '(',
  '((\\d{3})((((\\s|-)?\\d{3}){2})|((\\s|-)?\\d{4}(\\s|-)?\\d{2})))',
  '|(6(\\s|-)?\\d{8})', // mobile
  ')',
  ')',
  ')',
  '$',
].join(''));

export {
  phoneRegex,
  kboRegex,
  ovoRegex,
  nisRegex,
  dateFormat,
};
