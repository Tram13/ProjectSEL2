const proposalStatusTranslator = {
  draft: 'Ontwerp',
  completed: 'Ingediend',
  in_review: 'In review',
  pending_feedback: 'Wachtend op feedback',
  accepted: 'Goedgekeurd',
  denied: 'Afgewezen',
  cancelled: 'Ingetrokken',
};

const userRoleTranslator = {
  customer: 'Gebruiker',
  employee: 'Magda medewerker',
  admin: 'Magda admin',
};

const memberRoleTranslator = {
  manager: 'Manager',
  member: 'Lid',
};

const contactRoleTranslator = {
  submitter: 'Indiener',
  business: 'Business',
  business_backup: 'Business (back-up)',
  technical: 'Technisch',
  technnical_backup: 'Techisch (back-up)',
  safety_consultant: 'Veiligheidsconsulent',
  service_provider: 'Dienstenleverancier',
  responsible_d2d_managent_customer: 'Verantwoordlijke Dagelijks Bestuur Afnemer',
  manager_geosecure: 'Beheer Geosecure (Documentendienst)',
};

const reverseContactRoleTranslator = {
  Indiener: 'submitter',
  Business: 'business',
  'Business (back-up)': 'business_backup',
  Technisch: 'technical',
  'Techisch (back-up)': 'technnical_backup',
  Veiligheidsconsulent: 'safety_consultant',
  Dienstenleverancier: 'service_provider',
  'Verantwoordlijke Dagelijks Bestuur Afnemer': 'responsible_d2d_managent_customer',
  'Beheer Geosecure (Documentendienst)': 'manager_geosecure',
};

export {
  memberRoleTranslator,
  proposalStatusTranslator,
  userRoleTranslator,
  contactRoleTranslator,
  reverseContactRoleTranslator,
};
