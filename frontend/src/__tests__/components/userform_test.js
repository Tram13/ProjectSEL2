/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import {
  render, fireEvent, cleanup, waitFor,
} from '@testing-library/vue';
import UserForm from '@/components/user/UserForm.vue';
import { authUser, buildStore } from '@/test-lib';

const newUsersValid = [
  {
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    role: 'admin',
    password: 'ICUP999h',
  },
  {
    firstName: 'Peter',
    lastName: 'Selie',
    email: 'peter.selie@gmail.com',
    role: 'employee',
    password: 'Pjotr-2000',
  },
  {
    firstName: 'Paus',
    lastName: 'Urbanus',
    email: 'paus.urbanus@gmail.com',
    role: 'customer',
    password: '8H_dkL2b',
  },
];

const newUsersInvalid = [
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user@invalid', // invalid
    role: 'admin',
    password: 'ICUP999h',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user@.com', // invalid
    role: 'admin',
    password: 'ICUP999h',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com', // invalid
    role: 'admin',
    password: 'ICUP999h',
    invalid: 'email',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'A1aaaaa', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'asdfdsdsdsf', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'SDFDHGFHFHDG', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: '65165165151', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'SDGDGDF5165', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'df6sdf46sdf4', // invalid
    invalid: 'password',
  },
  {
    firstName: 'invalid',
    lastName: 'user',
    email: 'invalid.user.com',
    role: 'admin',
    password: 'jiVLlkjJoij', // invalid
    invalid: 'password',
  },
];

const errorMessages = {
  missing: {
    firstName: /voornaam ontbreekt/i,
    lastName: /achternaam ontbreekt/i,
    email: /email ontbreekt/i,
    password: /wachtwoord ontbreekt/i,
  },
  invalid: {
    firstName: /ongeldige voornaam/i,
    lastName: /ongeldige achternaam/i,
    email: /ongeldige email/i,
    password: /wachtwoord moet/i,
    passwordConfirm: /wachtwoord.+(niet )|(on)bevestigd/i,
  },
};

const events = {
  submit: 'user_form_close',
};

function newUserInputValidationTest(registration) {
  return async () => {
    const {
      getByTestId,
      findByTestId,
      queryByTestId,
      findByText,
      queryByText,
      findAllByText,
      emitted,
    } = render(UserForm, {
      global: {
        plugins: registration
          ? [buildStore({}), { registration: 'True' }]
          : [buildStore({ user: authUser.admin }), {}],
      },
    });
    const submitButton = getByTestId('submit-button');
    expect(submitButton).toHaveAttribute('type', 'submit');
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    const userFirstNameInput = getByTestId('user-firstName-input');
    const userLastNameInput = getByTestId('user-lastName-input');
    const userEmailInput = getByTestId('user-email-input');
    if (registration) {
      expect(await queryByTestId('user-role-input')).not.toBeInTheDocument();
    } else {
      expect(await findByTestId('user-role-input'));
    }
    const userPasswordInput = getByTestId('user-password-input');
    const userPasswordConfirm = getByTestId('user-password-confirm-input');
    // valid input
    for (let i = 0; i < newUsersValid.length; i += 1) {
      const user = newUsersValid[i];
      // firstname
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
      expect(await findByText(errorMessages.missing.firstName));
      await fireEvent.update(userFirstNameInput, user.firstName);
      expect(await queryByText(errorMessages.missing.firstName)).not.toBeInTheDocument();
      expect(await queryByText(errorMessages.invalid.firstName)).not.toBeInTheDocument();
      // lastname
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
      expect(await findByText(errorMessages.missing.lastName));
      await fireEvent.update(userLastNameInput, user.lastName);
      expect(await queryByText(errorMessages.missing.lastName)).not.toBeInTheDocument();
      expect(await queryByText(errorMessages.invalid.lastName)).not.toBeInTheDocument();
      // email
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
      expect(await findByText(errorMessages.missing.email));
      await fireEvent.update(userEmailInput, user.email);
      expect(await queryByText(errorMessages.missing.email)).not.toBeInTheDocument();
      expect(await queryByText(errorMessages.invalid.email)).not.toBeInTheDocument();
      // password
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
      expect(await findByText(errorMessages.missing.password));
      await fireEvent.update(userPasswordInput, user.password);
      expect(await queryByText(errorMessages.missing.password)).not.toBeInTheDocument();
      expect(await queryByText(errorMessages.invalid.password)).not.toBeInTheDocument();
      // password confirmation
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
      expect(await findByText(errorMessages.invalid.passwordConfirm));
      await fireEvent.update(userPasswordConfirm, user.password);
      expect(await queryByText(errorMessages.invalid.passwordConfirm)).not.toBeInTheDocument();
      // submit
      await fireEvent.click(submitButton);
      // emit happens in async function, so waitFor
      await waitFor(() => {
        try {
          expect(emitted()).toHaveProperty(events.submit);
        } catch (err) {
          err.message = `valid input was rejected: ${JSON.stringify(user)}\n`;
          throw err;
        }
      });
      delete emitted()[events.submit];
      // clear input
      await fireEvent.update(userFirstNameInput, '');
      await fireEvent.update(userLastNameInput, '');
      await fireEvent.update(userEmailInput, '');
      await fireEvent.update(userPasswordInput, '');
      // try submit again
      await fireEvent.click(submitButton);
      expect(emitted()).not.toHaveProperty(events.submit);
    }
    // invalid input
    for (let i = 0; i < newUsersInvalid.length; i += 1) {
      const user = newUsersInvalid[i];
      await fireEvent.update(userFirstNameInput, user.firstName);
      await fireEvent.update(userLastNameInput, user.lastName);
      await fireEvent.update(userEmailInput, user.email);
      await fireEvent.update(userPasswordInput, user.password);
      await fireEvent.update(userPasswordConfirm, user.password);
      // try to submit
      await fireEvent.click(submitButton);
      try {
        expect(emitted()).not.toHaveProperty(events.submit);
      } catch (err) {
        err.message = `invalid input was accepted: ${JSON.stringify(user)}\n`;
        throw err;
      }
      // check if error message is displayed
      expect(await findAllByText(errorMessages.invalid[user.invalid]));
    }
  };
}

test('input validation (registration)', newUserInputValidationTest(true));
test('input validation (create user as admin)', newUserInputValidationTest(false));
