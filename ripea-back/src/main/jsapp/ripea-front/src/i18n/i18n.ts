import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import translationCa from './translationCa';
import translationEn from './translationEn';
import translationEs from './translationEs';

const resources = {
    ca: { translation: translationCa },
    en: { translation: translationEn },
    es: { translation: translationEs },
};

i18next.use(LanguageDetector).use(initReactI18next).init({
    resources,
    fallbackLng: 'ca',
    interpolation: {
        escapeValue: false
    }
});

export default i18next;