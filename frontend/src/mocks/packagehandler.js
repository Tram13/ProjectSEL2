import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const packs = [
  {
    id: 0,
    name: 'Basispakket LED',
    deprecated: false,
    services: [
      {
        source: 'LED',
        deliveryMethod: 'FTP',
        href: 'https://sel2-N.ugent.be/api/services/2',
      },
      {
        source: 'LED',
        deliveryMethod: 'FTP',
        href: 'https://sel2-N.ugent.be/api/services/3',
      },
      {
        source: 'LED',
        deliveryMethod: 'FTP',
        href: 'https://sel2-N.ugent.be/api/services/4',
      },
    ],
  },
];

const pack = {
  id: 0,
  name: 'Basispakket LED',
  deprecated: false,
  services: [
    {
      source: 'LED',
      deliveryMethod: 'FTP',
      href: 'https://sel2-N.ugent.be/api/services/2',
    },
    {
      source: 'LED',
      deliveryMethod: 'FTP',
      href: 'https://sel2-N.ugent.be/api/services/3',
    },
    {
      source: 'LED',
      deliveryMethod: 'FTP',
      href: 'https://sel2-N.ugent.be/api/services/4',
    },
  ],
  created: '2021-05-07T18:03:03.571Z',
  lastUpdated: '2021-05-07T18:03:03.571Z',
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/packages/0',
    },
    packages: {
      href: 'https://sel2-N.ugent.be/api/packages',
    },
  },
};

const services = [
  {
    service: {
      id: 2,
      name: 'LED.PubliceerBewijs-02.00',
      deprecated: false,
      domain: 'LED',
      description: 'Deze dienst is een Publicatie-service. Elke afnemer heeft de mogelijkheid om in samenspraak met Informatie Vlaanderen te bepalen welke gegevens gevraagd worden, op basis van welke criteria deze geselecteerd moeten worden en met welke frequentie die zouden moeten aangeleverd worden. De gegevens worden na publicatie beschikbaar gesteld op de MAGDA FTP server. De exacte locatie van deze folder en de naam wordt in samenspraak met Informatie Vlaanderen bij aansluiting beslist. De folderstructuur en de manier waarop de gegevens opgehaald kunnen worden, wordt beschreven in /wiki/spaces/MG/pages/486015072.',
      sources: [
        'LED',
      ],
      deliveryMethods: [
        'WEBSERVICE', 'FTP',
      ],
      needsPermissions: true,
      source: 'LED',
      deliveryMethod: 'FTP',
    },
    source: 'LED',
    deliveryMethod: 'FTP',
  },
  {
    service: {
      id: 3,
      name: 'LED.GeefBewijs-02.00',
      deprecated: false,
      domain: 'LED',
      description: 'Het INSZ van de persoon waarvoor de bewijzen worden opgevraagd. Dit moet het actuele INSZ zijn. Indien dit niet het geval is dan zal de vraag niet succesvol zijn.',
      sources: [
        'LED',
      ],
      deliveryMethods: [
        'WEBSERVICE', 'FTP',
      ],
      needsPermissions: true,
      source: 'LED',
      deliveryMethod: 'FTP',
    },
    source: 'LED',
    deliveryMethod: 'FTP',
  },
  {
    service: {
      id: 4,
      name: 'LED.AnnuleerBewijs-02.00',
      deprecated: false,
      domain: 'LED',
      description: 'Het doel van het LED project is het bouwen van een authentieke gegevensdatabank met informatie over leer- en ervaringsbewijzen. Deze databank zal gevoed kunnen worden door (al dan niet) authentieke bronnen die op de centrale databank zijn aangesloten en kunnen worden bevraagd door gemachtigde belanghebbenden. De nood aan een dergelijke databank kwam onder meer naar boven in het beleidsdomein O&V in het kader van de financiering van het leerplichtonderwijs.',
      sources: [
        'LED',
      ],
      deliveryMethods: [
        'WEBSERVICE', 'FTP',
      ],
      needsPermissions: true,
      source: 'LED',
      deliveryMethod: 'FTP',
    },
    source: 'LED',
    deliveryMethod: 'FTP',
  },
];

export default [
  rest.get(endpoints.packages.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const pcks = (skip && limit) ? packs.slice(skip, skip + limit) : packs;
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          packageList: pcks,
          count: pcks.length,
          total: packs.length,
        },
        _links: {
          self: {
            href: endpoints.packages.href,
          },
        },
      }),
    );
  }),
  rest.get(/\/packages\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(pack),
  )),
  rest.get(/\/packages\/[^\/]+\/services/, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const servs = (skip && limit) ? services.slice(skip, skip + limit) : services;
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          packageServiceList: servs,
          count: servs.length,
          total: services.length,
        },
        _links: {
          self: {
            href: 'https://sel2-N.ugent.be/api/packages/0/services',
          },
        },
      }),
    );
  }),
];
