import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const services = [
  {
    id: 0,
    name: 'Dossier.VerwijderDossier-02.00',
    deprecated: false,
    domain: 'Dossier',
    description: 'Een entiteit kan via de dienst VerwijderDossier dossiers verwijderen waarvan die zelf de beheerder is.',
    sources: [
      'AIV',
    ],
    deliveryMethods: [
      'FTP',
    ],
    needsPermissions: true,
  },
  {
    id: 1,
    name: 'Energie.GeefEPC',
    deprecated: false,
    domain: 'Energie',
    description: 'Dit document beschrijft het gebruik en de werking van de webservice GeefEpc zoals deze door Agentschap Informatie Vlaanderen op het MAGDA platform aangeboden wordt.',
    sources: [
      'FAMIFED', 'KBO',
    ],
    deliveryMethods: [
      'PUB', 'MO',
    ],
    needsPermissions: true,
  },
  {
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
  },
  {
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
  },
  {
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
  },
];

const service = {
  id: 1,
  name: 'Energie.GeefEPC',
  deprecated: false,
  domain: 'Energie',
  description: 'Dit document beschrijft het gebruik en de werking van de webservice GeefEpc zoals deze door Agentschap Informatie Vlaanderen op het MAGDA platform aangeboden wordt.',
  sources: [
    'FAMIFED', 'KBO',
  ],
  deliveryMethods: [
    'PUB', 'MO',
  ],
  needsPermissions: true,
  created: '2021-05-08T17:15:26.128Z',
  lastUpdated: '2021-05-08T17:15:26.128Z',
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/services/1',
    },
    services: {
      href: 'https://sel2-N.ugent.be/api/services',
    },
  },
};

export default [
  rest.get(endpoints.services.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const servs = (skip && limit) ? services.slice(skip, skip + limit) : services;
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          serviceList: servs,
          count: servs.length,
          total: services.length,
        },
        _links: {
          self: {
            href: endpoints.services.href,
          },
        },
      }),
    );
  }),
  rest.get(/\/services\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(service),
  )),
];
