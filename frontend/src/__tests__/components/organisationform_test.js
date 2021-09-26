/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, emitted } from '@testing-library/vue';
import OrganisationForm from '@/components/organisation/OrganisationForm.vue';
import { buildStore } from '@/test-lib';

const newOrgsValid = [
  {
    name: 'De Vleesindustrie NV',
    kbo: '0315686352',
    ovo: 'OVO018954',
    nis: '44021',
    serviceProvider: 'provider',
  },
  {
    name: 'Cabbage Corp',
    kbo: '1856546514',
    ovo: 'OVO731689',
    nis: '31005',
    serviceProvider: 'provider',
  },
];

const newOrgsInvalid = [
  {
    organisationName: 'Pear',
    kbo: '5315686350', // invalid
    ovo: 'OVO138136',
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'kbo',
  },
  {
    organisationName: 'Bison',
    kbo: '1395161561621', // invalid
    ovo: 'OVO138136',
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'kbo',
  },
  {
    organisationName: 'Gopnik inc',
    kbo: '06519', // invalid
    ovo: 'OVO138136',
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'kbo',
  },
  {
    organisationName: 'Druzhnik',
    kbo: '065654a513', // invalid
    ovo: 'OVO138136',
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'kbo',
  },
  {
    organisationName: 'C-world',
    kbo: '1656548517',
    ovo: 'EVA138136', // invalid
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'ovo',
  },
  {
    organisationName: 'Guugel',
    kbo: '1656548514',
    ovo: '831138136', // invalid
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'ovo',
  },
  {
    organisationName: 'wookiepedia',
    kbo: '1656548510',
    ovo: 'OVO1381360', // invalid
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'ovo',
  },
  {
    organisationName: 'monsters&co',
    kbo: '1656548516',
    ovo: 'OVO13813', // invalid
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'ovo',
  },
  {
    organisationName: 'Pokeren met de Poepa inc',
    kbo: '1656548517',
    ovo: 'OVO13a137', // invalid
    nis: '44021',
    serviceProvider: 'provider',
    invalid: 'ovo',
  },
  {
    organisationName: 'Ikea',
    kbo: '1656548511',
    ovo: 'OVO132137',
    nis: '666', // invalid
    serviceProvider: 'provider',
    invalid: 'nis',
  },
  {
    organisationName: 'Andere Zee Sapkowski',
    kbo: '1656548516',
    ovo: 'OVO132137',
    nis: '10100101110', // invalid
    serviceProvider: 'provider',
    invalid: 'nis',
  },
  {
    organisationName: 'Proximus',
    kbo: '1656548519',
    ovo: 'OVO132137',
    nis: 'abcde', // invalid
    serviceProvider: 'provider',
    invalid: 'nis',
  },
];

const errorMessages = {
  missing: {
    organisationName: /naam ontbreekt/i,
    kbo: /kbo nummer ontbreekt/i,
    ovo: /ovo code ontbreekt/i,
    nis: /nis nummer ontbreekt/i,
    serviceProvider: /dienstenleverancier ontbreekt/i,
  },
  invalid: {
    kbo: /ongeldig kbo nummer/i,
    ovo: /ongeldige ovo code/i,
    nis: /ongeldig nis nummer/i,
  },
};

const events = {
  submit: 'organisation_form_close',
};

test('input validation', async () => {
  const {
    getByTestId,
    findByText,
    queryByText,
    findAllByText,
    emitted,
  } = render(OrganisationForm, {
    global: { plugins: [buildStore({})] },
  });
  const submitButton = getByTestId('submit-button');
  expect(submitButton).toHaveAttribute('type', 'submit');
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  const nameInput = getByTestId('org-name-input');
  const kboInput = getByTestId('org-kbo-input');
  const ovoInput = getByTestId('org-ovo-input');
  const nisInput = getByTestId('org-nis-input');
  const providerInput = getByTestId('org-provider-input');
  // valid input
  for (let i = 0; i < newOrgsValid.length; i += 1) {
    const org = newOrgsValid[i];
    // organisation name
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.organisationName));
    await fireEvent.update(nameInput, org.organisationName);
    expect(await queryByText(errorMessages.missing.organisationName)).not.toBeInTheDocument();
    // KBO
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.kbo));
    await fireEvent.update(kboInput, org.kbo);
    expect(await queryByText(errorMessages.missing.kbo)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.kbo)).not.toBeInTheDocument();
    // OVO
    await fireEvent.click(submitButton);
    await fireEvent.update(ovoInput, org.ovo);
    expect(await queryByText(errorMessages.missing.ovo)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.ovo)).not.toBeInTheDocument();
    // NIS
    await fireEvent.click(submitButton);
    await fireEvent.update(nisInput, org.nis);
    expect(await queryByText(errorMessages.missing.nis)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.nis)).not.toBeInTheDocument();
    // service provider
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.serviceProvider));
    await fireEvent.update(providerInput, org.serviceProvider);
    expect(await queryByText(errorMessages.missing.serviceProvider)).not.toBeInTheDocument();
    // submit
    await fireEvent.click(submitButton);
    try {
      expect(emitted()).toHaveProperty(events.submit);
    } catch (err) {
      err.message = `valid input was rejected: ${JSON.stringify(org)}\n`;
      throw err;
    }
    delete emitted()[events.submit];
    // clear input
    await fireEvent.update(nameInput, '');
    await fireEvent.update(kboInput, '');
    await fireEvent.update(ovoInput, '');
    await fireEvent.update(nisInput, '');
    await fireEvent.update(providerInput, '');
    // try submit again
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
  }
  // invalid input
  for (let i = 0; i < newOrgsInvalid.length; i += 1) {
    const org = newOrgsInvalid[i];
    await fireEvent.update(nameInput, org.organisationName);
    await fireEvent.update(kboInput, org.kbo);
    await fireEvent.update(ovoInput, org.ovo);
    await fireEvent.update(nisInput, org.nis);
    await fireEvent.update(providerInput, org.serviceProvider);
    // try to submit
    await fireEvent.click(submitButton);
    try {
      expect(emitted()).not.toHaveProperty(events.submit);
    } catch (err) {
      err.message = `invalid input was accepted: ${JSON.stringify(org)}\n`;
      throw err;
    }
    // check if error message is displayed
    expect(await findAllByText(errorMessages.invalid[org.invalid]));
  }
});
