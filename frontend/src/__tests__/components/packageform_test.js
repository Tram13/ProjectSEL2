/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, cleanup, waitFor } from '@testing-library/vue';
import PackageForm from '@/components/package/PackageForm.vue';
import { setupServer } from 'msw/node';
import {
  buildStore, buildTable, checkTable, table2string,
} from '@/test-lib';

// mws handlers
import packageHandlers from '@/mocks/packagehandler';
import serviceHandlers from '@/mocks/servicehandler';

const server = setupServer(...packageHandlers, ...serviceHandlers);

const newPackagesValid = [
  {
    name: 'valid package',
    services: [
      'Dossier.VerwijderDossier-02.00',
      'Energie.GeefEPC',
    ],
  },
];

const errorMessages = {
  missing: {
    name: /naam ontbreekt/i,
    services: /minstens één dienst/i,
  },
};

const events = {
  close: 'package_form_close',
  submit: 'package_form_submit',
};

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('input validation', async () => {
  const store = buildStore({});
  const {
    getByTestId,
    findByText,
    queryByText,
    getByRole,
    getAllByRole,
    emitted
  } = render(PackageForm, {
    global: {plugins: [store]}
  });
  const submitButton = getByTestId('submit-button');
  expect(submitButton).toHaveAttribute('type', 'submit');
  const nameInput = getByTestId('package-name-input');
  await waitFor(() => {
    expect(store.state.packagestore.package.loading).toBe(false);
    expect(store.state.servicestore.services.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'serviceVerbose')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  const rows = getAllByRole('row');
  const headers = [...rows[0].cells].map((cell) => (cell.textContent));
  // get indices to index table-rows
  const checkboxIndex = headers.findIndex((header) => (/voeg toe/i).test(header));
  const nameIndex = headers.findIndex((header) => (/naam/i).test(header));
  // valid input
  for (let i=0; i<newPackagesValid.length; i++) {
    const pack = newPackagesValid[i];
    // name
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.name));
    await fireEvent.update(nameInput, pack.name);
    expect(await queryByText(errorMessages.missing.name)).not.toBeInTheDocument();
    // services
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.services));
    for (const service of pack.services) {
      const index = rows.findIndex((row) => row.cells[nameIndex].textContent === service);
      await fireEvent.update(rows[index].cells[checkboxIndex].children[0]);
    }
    expect(await queryByText(errorMessages.missing.services)).not.toBeInTheDocument();
    // submit
    await fireEvent.click(submitButton);
    try {
      expect(emitted()).toHaveProperty(events.submit);
    } catch (err) {
      err.message = `valid input was rejected: ${JSON.stringify(pack)}\n`;
      throw err;
    }
    delete emitted()[events.submit];
    // clear input
    await fireEvent.update(nameInput, '');
    for (const service of pack.services) {
      const index = rows.findIndex((row) => row.cells[nameIndex].textContent === service);
      await fireEvent.update(rows[index].cells[checkboxIndex].children[0]);
    }
    // try submit again
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
  }
});
