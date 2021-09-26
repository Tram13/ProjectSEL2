import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: () => {
      const role = localStorage.getItem('role');
      if (role && (role === 'admin' || role === 'employee')) {
        return { name: 'MagdaHome' };
      }
      if (role && (role === 'customer')) {
        return { name: 'UserHome' };
      }
      return { name: 'NoLoggedInUserHome' };
    },
  },
  {
    path: '/',
    name: 'NoLoggedInUserHome',
    meta: {
      requiresAuth: false,
      roles: [],
    },
    component: () => import('../views/homes/NoLoggedInUserHome.vue'),
  },
  {
    path: '/',
    name: 'UserHome',
    meta: {
      requiresAuth: false,
      roles: [],
    },
    component: () => import('../views/homes/UserHome.vue'),
  },
  {
    path: '/',
    name: 'MagdaHome',
    meta: {
      requiresAuth: true,
      roles: [],
    },
    component: () => import('../views/homes/MagdaHome.vue'),
  },
  {
    path: '/aanmelden',
    name: 'Login',
    component: () => import('../views/account/Login.vue'),
  },
  {
    path: '/registreren',
    name: 'Registration',
    component: () => import('../views/account/Registration.vue'),
  },
  {
    path: '/gebruiker',
    name: 'User',
    component: () => import('../views/user/User.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/gebruiker/create',
    name: 'CreateUser',
    component: () => import('../views/user/CreateUser.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/gebruiker/:id',
    name: 'InspectUser',
    component: () => import('../views/user/InspectUser.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/organisatie',
    name: 'Organisation',
    component: () => import('../views/organisation/Organisation.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/organisatie/create',
    name: 'CreateOrganisation',
    component: () => import('../views/organisation/CreateOrganisation.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/organisatie/:id',
    name: 'InspectOrganisation',
    component: () => import('../views/organisation/InspectOrganisation.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag',
    name: 'Proposal',
    component: () => import('../views/proposal/Proposal.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/create',
    name: 'CreateProposal',
    component: () => import('../views/proposal/CreateProposal.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/inleiding',
    name: 'InspectProposalIntroduction',
    component: () => import('../views/proposal/proposalPages/ProposalIntroduction.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/contact',
    name: 'InspectProposalContact',
    component: () => import('../views/proposal/proposalPages/ProposalContact.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/beschrijving',
    name: 'InspectProposalDescription',
    component: () => import('../views/proposal/proposalPages/ProposalDescription.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/aanvraag/:id/beschrijving',
    name: 'InspectProposalDescription',
    component: () => import('../views/proposal/proposalPages/ProposalDescription.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/aanvraag/:id/context',
    name: 'InspectProposalContext',
    component: () => import('../views/proposal/proposalPages/ProposalContext.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/pakketten',
    name: 'InspectProposalPackages',
    component: () => import('../views/proposal/proposalPages/ProposalPackages.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/diensten',
    name: 'InspectProposalServices',
    component: () => import('../views/proposal/proposalPages/ProposalServices.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/functioneel',
    name: 'InspectProposalFunctional',
    component: () => import('../views/proposal/proposalPages/ProposalFunctional.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/streefdata',
    name: 'InspectProposalSuplements',
    component: () => import('../views/proposal/proposalPages/ProposalSupplements.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/aanvraag/:id/certificaten',
    name: 'CreateCert',
    component: () => import('../views/proposal/proposalPages/CreateCert.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/dienst',
    name: 'Service',
    component: () => import('../views/service/Service.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/dienst/:id',
    name: 'InspectService',
    component: () => import('../views/service/InspectService.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/pakket',
    name: 'Package',
    component: () => import('../views/package/Package.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/pakket/:id',
    name: 'InspectPackage',
    component: () => import('../views/package/InspectPackage.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/statistieken',
    name: 'Permission',
    component: () => import('../views/statistics/Statistics.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/machtiging/create',
    name: 'CreatePermission',
    component: () => import('../views/permission/CreatePermission.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/machtiging/:id',
    name: 'InspectPermission',
    component: () => import('../views/permission/InspectPermission.vue'),
    meta: {
      requiresAuth: true,
      roles: [],
    },
  },
  {
    path: '/error',
    name: 'error',
    component: () => import('../views/error.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: () => import('../views/404.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.VUE_APP_HOST || '/app/'),
  routes,
});

router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('credentials');
  if (to.matched.some((record) => record.meta.requiresAuth) && !loggedIn) {
    return next('/aanmelden');
  }
  return next();
});

export default router;
