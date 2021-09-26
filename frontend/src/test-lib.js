import store from '@/store';
import { dateFormat } from '@/helpers/regexhelper';

const endpoints = {
  self: {
    href: 'https://sel2-N.ugent.be/api',
  },
  auth: {
    href: 'https://sel2-N.ugent.be/api/auth',
  },
  certificates: {
    href: 'https://sel2-N.ugent.be/api/certificates',
  },
  config: {
    href: 'https://sel2-N.ugent.be/api/config',
  },
  organisations: {
    href: 'https://sel2-N.ugent.be/api/organisations',
  },
  packages: {
    href: 'https://sel2-N.ugent.be/api/packages',
  },
  permissions: {
    href: 'https://sel2-N.ugent.be/api/permissions',
  },
  proposals: {
    href: 'https://sel2-N.ugent.be/api/proposals',
  },
  services: {
    href: 'https://sel2-N.ugent.be/api/services',
  },
  users: {
    href: 'https://sel2-N.ugent.be/api/users',
  },
  files: {
    href: 'https://sel2-N.ugent.be/api/files',
  },
};

const authUser = {
  customer: {
    id: 11,
    firstName: 'Mike',
    lastName: 'Oxlong',
    email: 'mike.oxlong@gmail.com',
    role: 'customer',
    password: 'A1aaaaaa', // NOSONAR
    _links: {
      self: {
        href: 'https://sel2-N.ugent.be/api/users/11',
      },
      users: {
        href: 'https://sel2-N.ugent.be/api/users',
      },
      organisations: {
        href: 'https://sel2-N.ugent.be/api/users/11/organisations',
      },
    },
  },
  admin: {
    id: 12,
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    role: 'admin',
    password: 'A1aaaaaa', // NOSONAR
    _links: {
      self: {
        href: 'https://sel2-N.ugent.be/api/users/12',
      },
      users: {
        href: 'https://sel2-N.ugent.be/api/users',
      },
      organisations: {
        href: 'https://sel2-N.ugent.be/api/users/12/organisations',
      },
    },
  },
};

const emailFormat = /^.+@.+$/;

const tableEntryValidators = {
  user: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Rol: /^(gebruiker)|(magda medewerker)|(magda admin)$/i,
  },
  customer: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Rol: /^gebruiker$/i,
  },
  employee: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Rol: /^magda medewerker$/i,
  },
  admin: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Rol: /^magda admin$/i,
  },
  contact: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Telefoonnummer: /^\d+$/,
  },
  proposalContact: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Telefoonnummer: /^\d+$/,
    Rol: /^$/,
  },
  member: {
    Voornaam: /^.+$/,
    Achternaam: /^.+$/,
    Email: emailFormat,
    Rol: /^(manager)|(lid)$/i,
  },
  organisation: {
    Naam: /^.+$/,
    NIS: /^\d{5}$/,
    OVO: /^OVO\d{6}$/,
    KBO: /^[01]\d{9}$/,
  },
  userOrganisation: {
    Naam: /^.+$/,
    Rol: /^(manager)|(lid)$/i,
    Status: /(goedgekeurd)|(niet goedgekeurd)$/i,
  },
  service: {
    Naam: /^.+$/,
  },
  serviceVerbose: {
    Naam: /^.+$/,
    Beschrijving: /^.+$/,
  },
  package: {
    Naam: /^.+$/,
  },
  proposal: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^(ontwerp)|(ingediend)|(goedgekeurd)|(afgewezen)|(in review)$/i,
  },
  inReview: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^in review$/i,
  },
  draft: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^ontwerp$/i,
  },
  accepted: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^goedgekeurd$/i,
  },
  denied: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^afgewezen$/i,
  },
  completed: {
    Naam: /^.+$/,
    Deadline: dateFormat,
    Status: /^ingediend$/i,
  },
  permission: {
    Naam: /^.+$/,
    Code: /^.+$/,
    Link: /^.*$/,
  },
  certificate: {
    Aanvraag: /^.+$/,
    Aangemaakt: dateFormat,
    'Laatst gewijzigd': dateFormat,
    Download: /^.*$/,
  },
};

function buildStore({ user }) {
  store.commit('SET_ENDPOINTS', {
    _links: endpoints,
  });
  if (user) {
    store.commit('ADD_USER', user);
    store.commit('SET_AUTH_USER', user);
    store.commit('SET_SESSION_DATA', { sessionToken: 'a', refreshToken: 'a' });
  }
  return store;
}

/*
* converts a HTMLTableElement to an easier to work with object
*/
function buildTable(table) {
  const output = [];
  const fields = [];
  let row;
  let entry;
  for (const cell of table.rows[0].cells) {
    fields.push(cell.textContent);
  }
  for (let i = 1; i < table.rows.length; i++) {
    row = table.rows[i];
    entry = {};
    for (let j = 0; j < row.cells.length; j++) {
      entry[fields[j]] = row.cells[j].textContent;
    }
    output.push(entry);
  }
  return output;
}

/*
* checks if a table has the desired content
*/
function checkTable(table, type) {
  const fields = tableEntryValidators[type];
  return table.length > 0 && table.every(
    (item) => Object.keys(fields).every(
      (field) => fields[field].test(item[field]),
    ),
  );
}

/*
* converts a table to a string
*/
function table2string(table) {
  let output = '';
  table.forEach((entry) => {
    output += `${JSON.stringify(entry)}\n`;
  });
  return output;
}

export {
  buildStore,
  buildTable,
  checkTable,
  table2string,
  endpoints,
  authUser,
};
