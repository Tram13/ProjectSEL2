/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, cleanup } from '@testing-library/vue';
import ServiceForm from '@/components/service/ServiceForm.vue';
import { buildStore } from '@/test-lib';

const newServicesValid = [
  {
    name: 'valid service',
    domain: 'domain',
    description: 'this is a valid service',
    sources: ['IPEX', 'BOSA'],
    deliveryMethods: ['WEBSERVICE', 'FTP'],
    needsPermission: false,
  },
  {
    name: 'valid service',
    domain: 'domain',
    description: 'this is a valid service',
    sources: ['IPEX', 'BOSA', 'RR', 'KSZ/RSZ'],
    deliveryMethods: ['WEBSERVICE', 'FTP', 'MO', 'PUB'],
    needsPermission: true,
  },
];

const errorMessages = {
  missing: {
    name: /naam ontbreekt/i,
    domain: /domein ontbreekt/i,
    description: /beschrijving ontbreekt/i,
    sources: /minstens één bron/i,
    deliveryMethods: /minstens één aanlevermethode/i,
  },
};

const events = {
  submit: 'service_form_close',
};

test('input validation', async () => {
  const {
    getByTestId,
    findByText,
    queryByText,
    getByRole,
    emitted,
  } = render(ServiceForm, {
    global: { plugins: [buildStore({})] },
  });
  const submitButton = getByTestId('submit-button');
  expect(submitButton).toHaveAttribute('type', 'submit');
  expect(emitted()).not.toHaveProperty(events.submit);
  const nameInput = getByTestId('service-name-input');
  const domainInput = getByTestId('service-domain-input');
  const descriptionInput = getByTestId('service-description-input');
  const permissionInput = getByTestId('service-permission-input');
  // valid input
  for (let i = 0; i < newServicesValid.length; i += 1) {
    const service = newServicesValid[i];
    // name
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.name));
    await fireEvent.update(nameInput, service.name);
    expect(await queryByText(errorMessages.missing.name)).not.toBeInTheDocument();
    // domain
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.domain));
    await fireEvent.update(domainInput, service.domain);
    expect(await queryByText(errorMessages.missing.domain)).not.toBeInTheDocument();
    // description
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.description));
    await fireEvent.update(descriptionInput, service.description);
    expect(await queryByText(errorMessages.missing.description)).not.toBeInTheDocument();
    // sources
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.sources));
    // check sources
    for (let j = 0; j < service.sources.length; j++) {
      await fireEvent.click(getByRole('checkbox', { name: service.sources[j] }));
    }
    expect(await queryByText(errorMessages.missing.sources)).not.toBeInTheDocument();
    // delivery methods
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
    expect(await findByText(errorMessages.missing.deliveryMethods));
    // check delivery methods
    for (let j = 0; j < service.deliveryMethods.length; j++) {
      await fireEvent.click(getByRole('checkbox', { name: service.deliveryMethods[j] }));
    }
    expect(await queryByText(errorMessages.missing.deliveryMethods)).not.toBeInTheDocument();
    // permission
    if (service.needsPermission) {
      await fireEvent.click(permissionInput);
    }
    // submit
    await fireEvent.click(submitButton);
    try {
      expect(emitted()).toHaveProperty(events.submit);
    } catch (err) {
      err.message = `valid input was rejected: ${JSON.stringify(service)}\n`;
      throw err;
    }
    delete emitted()[events.submit];
    // clear input
    await fireEvent.update(nameInput, '');
    await fireEvent.update(domainInput, '');
    await fireEvent.update(descriptionInput, '');
    // uncheck sources
    for (let j = 0; j < service.sources.length; j++) {
      await fireEvent.click(getByRole('checkbox', { name: service.sources[j] }));
    }
    // uncheck delivery methods
    for (let j = 0; j < service.deliveryMethods.length; j++) {
      await fireEvent.click(getByRole('checkbox', { name: service.deliveryMethods[j] }));
    }
    // uncheck permission
    if (service.needsPermission) {
      await fireEvent.click(permissionInput);
    }
    // try submit again
    await fireEvent.click(submitButton);
    expect(emitted()).not.toHaveProperty(events.submit);
  }
});
