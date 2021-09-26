/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/vue';
import ContactInspect from '@/components/contact/ContactInspect.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib';

// mws handlers
import organisationHandlers from '@/mocks/organisationhandler.js';

const server = setupServer(...organisationHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('inspect contact (customer & organisation manager)', async () => {
  const store = buildStore({ user: authUser.customer });
  const {
    findByText,
    getByRole
  } = render(ContactInspect, {
    global: {
      plugins: [store],
    },
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.contactstore.contact.loading).toBe(false);
  });
  expect(await findByText(/Mike/)).toBeInTheDocument();
  expect(await findByText(/Oxlong/)).toBeInTheDocument();
  expect(await findByText(/mike\.oxlong@gmail\.com/)).toBeInTheDocument();
  expect(await findByText(/053555555/)).toBeInTheDocument();
});
