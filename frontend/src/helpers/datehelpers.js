/* eslint no-param-reassign: 0 */
function formatDate(date) {
  if (typeof date === 'string') {
    date = new Date(date);
  } else if (!(date instanceof Date)) {
    throw new Error(`date must be a string or a Date object, was ${date}`);
  }
  const day = date.getDate();
  const month = date.getMonth() + 1;
  const year = date.getFullYear();
  return `${day < 10 ? '0' : ''}${day}-${month < 10 ? '0' : ''}${month}-${year}`;
}

export default formatDate;
