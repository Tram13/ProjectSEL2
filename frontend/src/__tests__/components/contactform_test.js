/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/vue';
import ContactForm from '@/components/contact/ContactForm.vue';
import { setupServer } from 'msw/node';
import { buildStore } from '@/test-lib.js';

const newContactsValid = [
  {
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    phoneNumber: '053267391',
  },
  {
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    phoneNumber: '053 26 73 91',
  },
  {
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    phoneNumber: '053-26-73-91',
  },
  {
    firstName: 'totaal arbitrair',
    lastName: 'telefoonnummer',
    email: 'totaal.arbitrair@gmail.com',
    phoneNumber: '+32471291355',
  },
  {
    firstName: 'totaal arbitrair',
    lastName: 'telefoonnummer',
    email: 'totaal.arbitrair@gmail.com',
    phoneNumber: '+32-471-2913-55',
  },
  {
    firstName: 'zijn',
    lastName: 'tegenhanger',
    email: 'zijn.tegenhanger@gmail.com',
    phoneNumber: '0471291355',
  },
  {
    firstName: 'Nederlands',
    lastName: 'Nummer',
    email: 'nederlands.nummer@gmail.com',
    phoneNumber: '+31655585820',
  },
  {
    firstName: 'Nederlands',
    lastName: 'Nummer',
    email: 'nederlands.nummer@gmail.com',
    phoneNumber: '+31 655 585 820',
  },
  {
    firstName: 'Nederlands',
    lastName: 'Nummer',
    email: 'nederlands.nummer@gmail.com',
    phoneNumber: '+31-655-585-820',
  },
  {
    firstName: 'Nederlands',
    lastName: 'Nummer',
    email: 'nederlands.nummer@gmail.com',
    phoneNumber: '+31-655-5477-24',
  },
];

const newContactsInvalid = [
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@invalid', // invalid
    phoneNumber: '053267391',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@.com', // invalid
    phoneNumber: '053267391',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact.com', // invalid
    phoneNumber: '053267391',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '05326739', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '053267390165155', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: 'hallo?', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '+324712913554', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '+324712913', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '+316555858207', // invalid
    invalid: 'phoneNumber',
  },
  {
    firstName: 'invalid',
    lastName: 'contact',
    email: 'invalid.contact@gmail.com',
    phoneNumber: '+3165558582', // invalid
    invalid: 'phoneNumber',
  },
];

const errorMessages = {
  missing: {
    firstName: /voornaam ontbreekt/i,
    lastName: /achternaam ontbreekt/i,
    email: /email ontbreekt/i,
    phoneNumber: /telefoonnummer ontbreekt/i,
  },
  invalid: {
    firstName: /ongeldige voornaam/i,
    lastName: /ongeldige achternaam/i,
    email: /ongeldige email/i,
    phoneNumber: /ongeldig telefoonnummer/i,
  },
};

const events = {
  submit: 'contact_form_submit',
};

test('input validation', async () => {
  const {
    getByTestId,
    findByText,
    queryByText,
    findAllByText,
    emitted,
  } = render(ContactForm, {
    global: {
      plugins: [buildStore({})],
    },
  });
  const submitButton = getByTestId('submit-button');
  expect(submitButton).toHaveAttribute('type', 'submit');
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  const firstNameInput = getByTestId('contact-firstName-input');
  const lastNameInput = getByTestId('contact-lastName-input');
  const emailInput = getByTestId('contact-email-input');
  const phoneInput = getByTestId('contact-phone-input');
  // valid input
  for (let i = 0; i < newContactsValid.length; i += 1) {
    const contact = newContactsValid[i];
    // firstname
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.firstName));
    await fireEvent.update(firstNameInput, contact.firstName);
    expect(await queryByText(errorMessages.missing.firstName)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.firstName)).not.toBeInTheDocument();
    // lastname
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.lastName));
    await fireEvent.update(lastNameInput, contact.lastName);
    expect(await queryByText(errorMessages.missing.lastName)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.lastName)).not.toBeInTheDocument();
    // email
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.email));
    await fireEvent.update(emailInput, contact.email);
    expect(await queryByText(errorMessages.missing.email)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.email)).not.toBeInTheDocument();
    // phone number
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.phoneNumber));
    await fireEvent.update(phoneInput, contact.phoneNumber);
    expect(await queryByText(errorMessages.missing.phoneNumber)).not.toBeInTheDocument();
    expect(await queryByText(errorMessages.invalid.phoneNumber)).not.toBeInTheDocument();
    // submit
    await fireEvent.click(submitButton);
    // emit happens in async function, so waitFor
    await waitFor(() => {
      try {
        expect(emitted()).toHaveProperty(events.submit);
      } catch (err) {
        err.message = `valid input was rejected: ${JSON.stringify(contact)}\n`;
        throw err;
      }
    })
    delete emitted()[events.submit];
    // clear input
    await fireEvent.update(firstNameInput, '');
    await fireEvent.update(lastNameInput, '');
    await fireEvent.update(emailInput, '');
    await fireEvent.update(phoneInput, '');
    // try submit again
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
  }
  // invalid input
  for (let i = 0; i < newContactsInvalid.length; i += 1) {
    const contact = newContactsInvalid[i];
    await fireEvent.update(firstNameInput, contact.firstName);
    await fireEvent.update(lastNameInput, contact.lastName);
    await fireEvent.update(emailInput, contact.email);
    await fireEvent.update(phoneInput, contact.phoneNumber);
    // try to submit
    await fireEvent.click(submitButton);
    try {
      expect(emitted()).not.toHaveProperty(events.submit);
    } catch (err) {
      err.message = `invalid input was accepted: ${JSON.stringify(contact)}\n`;
      throw err;
    }
    // check if error message is displayed
    expect(await findAllByText(errorMessages.invalid[contact.invalid]));
  }
});
