import { useStore } from 'vuex';

// clearFunctionName: String, name of function in store to clear entries
// fetchFunctionName: String, name of function in store to fetch and update entries
export function createUpdateFunction(clearFunctionName, fetchFunctionName) {
  // use the store for commit and dispatch
  const store = useStore();
  // 2 variables to keep state
  let currentSortBy = '';
  let currentSortReverse = false;
  // return a function that will be used to update the entries in paginations
  return async function updateFunction(skip, limit, sortBy, sortReverse, filterOptions) {
    // check if sortBy or order has to change
    if (currentSortBy !== sortBy || currentSortReverse !== sortReverse) {
      // if new sorting clear the store
      store.commit(clearFunctionName);
      // set the new sorting params
      currentSortBy = sortBy;
      currentSortReverse = sortReverse;
    }
    // call the store to update the entries
    const sortDirection = sortReverse ? 'desc' : 'asc';
    // return total amount of items in backend
    return store.dispatch(fetchFunctionName, {
      skip, limit, sortBy, sortDirection, ...filterOptions,
    });
  };
}

// clearFunctionName: String, name of function in store to clear entries
// fetchFunctionName: String, name of function in store to fetch and update entries
// extraParams: Object, extra parameters needed for apicall
export function createUpdateFunctionExtraParams(clearFunctionName, fetchFunctionName, extraParams) {
  // use the store for commit and dispatch
  const store = useStore();
  // 2 variables to keep state
  let currentSortBy = '';
  let currentSortReverse = false;
  // return a function that will be used to update the entries in paginations
  return async function updateFunction(skip, limit, sortBy, sortReverse, filterOptions) {
    // check if sortBy or order has to change
    if (currentSortBy !== sortBy || currentSortReverse !== sortReverse) {
      // if new sorting clear the store
      store.commit(clearFunctionName);
      // set the new sorting params
      currentSortBy = sortBy;
      currentSortReverse = sortReverse;
    }
    // call the store to update the entries
    const sortDirection = sortReverse ? 'desc' : 'asc';
    // return total amount of items in backend
    return store.dispatch(fetchFunctionName, {
      skip, limit, sortBy, sortDirection, ...filterOptions, ...extraParams,
    });
  };
}
