/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/vue';
import User from '@/views/user/User.vue';
import InspectUser from '@/views/user/InspectUser.vue';
import { setupServer } from 'msw/node';
import { authUser, buildStore, buildTable, checkTable, table2string } from '@/test-lib.js';

// mws handlers
import userHandlers from '@/mocks/userhandler.js';

const server = setupServer(...userHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test("inspect account details (customer)", async () => {
  const store = buildStore({ user: authUser.customer });
  const {
    findByText,
    getByRole
  } = render(InspectUser, {
    global: {
      plugins: [store],
    }
  });
  expect(await findByText(/Mike/)).toBeInTheDocument();
  expect(await findByText(/Oxlong/)).toBeInTheDocument();
  expect(await findByText(/mike\.oxlong@gmail\.com/)).toBeInTheDocument();
  expect(await findByText(/gebruiker/i)).toBeInTheDocument();
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.userstore.user.loading).toBe(false);
    expect(store.state.userstore.user.organisations.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'userOrganisation')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});

test("users page (admin)", async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
    getAllByRole,
    findByRole
  } = render(User, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByRole('heading', { name: 'Gebruiker' })).toBeInTheDocument();
  expect(await findByRole('button', { name: /gebruiker aanmaken/i })).toBeInTheDocument();
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(false);
  });
  let table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'user')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // customers only
  await fireEvent.click(getByRole('button', {name: /^gebruiker$/i}));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'customer')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // employees only
  await fireEvent.click(getByRole('button', {name: /^magda medewerker$/i}));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'employee')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // admins only
  await fireEvent.click(getByRole('button', {name: /^magda admin$/i}));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'admin')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // all users
  await fireEvent.click(getByRole('button', {name: /^alle$/i}));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.userstore.users.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'user')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
/*TODO: test pagination in datatable component
  // pagination buttons
  const buttons = getAllByRole('button', {name: /^\d+$/});
  for (let i=0; i<buttons.length; i++){
    await fireEvent.click(buttons[i]);
    // wait for loading screen to show
    await waitFor(() => {
      expect(store.state.userstore.users.loading).toBe(true);
    });
    // wait for request to be handled
    await waitFor(() => {
      expect(store.state.userstore.users.loading).toBe(false);
    });
    table = buildTable(getByRole('table'));
    try {
      // check if the table contains valid entries
      expect(checkTable(table, 'user')).toBe(true);
    } catch (err) {
      err.message = `invalid table entries:\n${table2string(table)}`;
      throw err;
    }
  };
  // pagination options
  const pagination = getByRole('select');
  const nums = ['1', '5'];
  for (let i=0; i<nums.length; i++){
    await fireEvent.update(pagination, nums[i]);
    // wait for loading screen to show
    await waitFor(() => {
      expect(store.state.userstore.users.loading).toBe(true);
    });
    // wait for request to be handled
    await waitFor(() => {
      expect(store.state.userstore.users.loading).toBe(false);
    });
    table = buildTable(getByRole('table'));
    // the table shouldn't contain more entries than specified by the pagination
    expect(table.length > parseInt(nums[i])).toBe(false);
    try {
      // check if the table contains valid entries
      expect(checkTable(table, 'user')).toBe(true);
    } catch (err) {
      err.message = `invalid table entries:\n${table2string(table)}`;
      throw err;
    }
  };
*/
});
