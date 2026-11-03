import { useEffect } from 'react';
import {useLocation} from 'react-router-dom';
import {useTranslation} from "react-i18next";
import i18n from './i18n/i18n';

const TITLES: any = {
    '/expedient': "navigate.expedient",
    '/expedientPeticio': "navigate.expedientPeticio",
    '/usuariTasca': "navigate.usuariTasca",

    '/entitat': "navigate.entitat",
    '/avis': "navigate.avis",
    '/pinbalServei': "page.user.menu.pinbal",
    '/config': "page.user.menu.props",
    '/excepcio': "navigate.exception",
    '/integracio': "navigate.integracio",
    '/usuari': "navigate.usuari",

    // Accions massives
    '/massiu/portafirmes': "navigate.massiu.portafirmes",
    '/massiu/firmasimpleweb': "navigate.massiu.firmasimpleweb",
    '/massiu/canviEstat': "navigate.massiu.canviEstat",
    '/massiu/tancament': "navigate.massiu.tancament",
    '/seguimentArxiuPendents': "navigate.massiu.seguimentArxiuPendents",
    '/massiu/csv': "navigate.massiu.csv",
    '/massiu/definitiu': "navigate.massiu.definitiu",
    '/massiu/canviPrioritats': "navigate.massiu.canviPrioritats",
    '/massiu/expedientPeticioCanviEstatDistribucio': "navigate.massiu.expedientPeticioCanviEstatDistribucio",
    '/massiu/procesarAnnexosPendents': "navigate.massiu.procesarAnnexosPendents",

    // Consultes
    '/contingutAdmin': "page.user.menu.continguts",
    '/historic': "page.user.menu.dadesEstadistiques",
    '/metaExpedientRevisio': "page.user.menu.revisar",
    '/seguimentPortafirmes': "page.user.menu.portafib",
    '/seguimentNotificacions': "page.user.menu.notib",
    '/seguimentPinbal': "page.user.menu.pinbalEnviades",
    '/seguimentTasques': "page.user.menu.assignacio",
    '/seguimentExpedientsPendents': "page.user.menu.pendents",
    '/expedientPeticioComunicades': "page.user.menu.comunicades",

    // Configurar
    '/metaExpedient': "page.user.menu.procedimentsTitle",
    // '/metaExpedient/:id/permis': "page.user.menu.procedimentPermis",
    '/tipusDocumental': "page.user.menu.nti",
    '/grup': "page.user.menu.grups",
    // '/grupPermis/:id/permis': "page.user.menu.grupPermis",
    '/organgestor': "page.user.menu.organs",
    // '/organgestor/:id/permis': "page.user.menu.organPermis",
    '/permis': "page.user.menu.permisos",
    '/domini': "page.user.menu.dominis",
    '/urlInstruccio': "page.user.menu.url",

    '/sitemap': "navigate.sitemap",
    '/accessibilitat': "navigate.accessibilitat",
};

/**
 * Títol de la pàgina actual. Es guarda la clau de traducció (i no el text traduït) perquè el títol
 * es pugui tornar a calcular quan canvia l'idioma: en carregar, l'idioma del perfil de l'usuari
 * s'aplica després que la pantalla hagi posat el títol, i l'usuari el pot canviar amb el selector.
 */
type TitolPagina = {
    /** Clau de traducció */
    clau?: string;
    /** Paràmetres de la traducció (dades: no es tradueixen) */
    params?: Record<string, any>;
    /** Text de dades que s'afegeix darrere la part traduïda (no es tradueix) */
    sufix?: string;
};

let titolActual: TitolPagina = {};

const aplicarTitol = () => {
    const traduit = titolActual.clau ? i18n.t(titolActual.clau, titolActual.params) : undefined;
    const title = [traduit, titolActual.sufix].filter(Boolean).join(' - ');
    document.title = 'RIPEA' + (title ? ` - ${title}` : '');
}

/**
 * Posa el títol de la pàgina a partir d'una clau de traducció. Si el títol inclou dades, han d'anar
 * als paràmetres o al sufix, no dins la clau ja traduïda.
 */
export const setTitlePage = (clau?: string, params?: Record<string, any>, sufix?: string) => {
    titolActual = { clau, params, sufix };
    aplicarTitol();
}

/** Posa un títol fet només de dades, que no s'ha de traduir (p. ex. el nom de l'expedient). */
export const setTitlePageText = (text?: string) => setTitlePage(undefined, undefined, text);

const TitleHeaderConfigurator = () => {
    const { i18n: i18nContext } = useTranslation();
    const location = useLocation();

    // Títol genèric de la ruta. Les pantalles de detall el substitueixen amb setTitlePage
    useEffect(() => {
        setTitlePage(TITLES[location.pathname]);
    }, [location.pathname]);

    // Un canvi d'idioma només retradueix el títol actual: no torna al genèric de la ruta
    useEffect(() => {
        aplicarTitol();
    }, [i18nContext.language]);

    return null;
};

export default TitleHeaderConfigurator;
