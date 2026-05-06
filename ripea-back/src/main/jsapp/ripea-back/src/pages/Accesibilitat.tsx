import React from 'react';
import {Box, Icon, Link, Typography} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {CardPage} from "../components/CardData.tsx";

const SectionTitle: React.FC<{
    icon?: string;
    color?: 'primary' | 'secondary' | 'success' | 'error' | 'warning' | 'info' | 'action' | 'disabled' | 'inherit';
    children: React.ReactNode;
}> = ({icon, color = 'success', children}) => (
    <Typography variant="h6" component="h2" display="flex" alignItems="center" gutterBottom sx={{mt: 1}}>
        {icon && <Icon color={color} sx={{fontSize: 'inherit'}}>{icon}</Icon>}
        {children}
    </Typography>
);

const Accesibilitat: React.FC = () => {
    const {t} = useTranslation();
    const p = 'page.accesibilitat';
    const rdUrl = 'https://www.boe.es/diario_boe/txt.php?id=BOE-A-2018-12699';

    return <CardPage header={<Typography variant="h4" component="h1">{t(`${p}.title`)}</Typography>}>
            <Box sx={{'& .MuiTypography-body1': {fontWeight: 400}}}>
                {/* Introducció */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="info" color="info">{t(`${p}.intro.title`)}</SectionTitle>
                    <Typography component="p">
                        {t(`${p}.intro.p1Part1`)}{' '}
                        <Link href={rdUrl} target="_blank" rel="noopener noreferrer">
                            {t(`${p}.intro.p1LinkText`)}
                        </Link>
                        {t(`${p}.intro.p1Part2`)}
                    </Typography>
                    <Typography component="p">
                        {t(`${p}.intro.p2Part1`)}{' '}
                        <Link href="https://intranet.caib.es/ripeaback/" target="_blank" rel="noopener noreferrer">
                            https://intranet.caib.es/ripeaback/
                        </Link>
                        {' '}{t(`${p}.intro.p2Part2`)}
                    </Typography>
                </Box>

                {/* Situació de compliment */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="check_circle" color="success">{t(`${p}.compliment.title`)}</SectionTitle>
                    <Typography component="p">
                        {t(`${p}.compliment.introPart1`)}{' '}
                        <Link href={rdUrl} target="_blank" rel="noopener noreferrer">
                            {t(`${p}.compliment.introLinkText`)}
                        </Link>
                        {' '}{t(`${p}.compliment.introPart2`)}
                    </Typography>
                </Box>

                {/* Contingut no accessible */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="cancel" color="error">{t(`${p}.noAccesible.title`)}</SectionTitle>
                    <Box component="ol">
                        {([1, 2, 3] as const).map(i => (
                            <Box component="li" key={i} sx={{mb: 1}}>
                                <Typography component="span" sx={{fontWeight: 600}}>
                                    {t(`${p}.noAccesible.item${i}.title`)}
                                </Typography>
                                <Typography component="p">{t(`${p}.noAccesible.item${i}.desc1`)}</Typography>
                                <Typography component="p">{t(`${p}.noAccesible.item${i}.desc2`)}</Typography>
                            </Box>
                        ))}
                    </Box>
                </Box>

                {/* Preparació */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="calendar_today" color="warning">{t(`${p}.preparacio.title`)}</SectionTitle>
                    <Typography component="p">{t(`${p}.preparacio.elaborat`)}</Typography>
                    <Box component="ul">
                        <Box component="li">{t(`${p}.preparacio.dataPrep`)}</Box>
                        <Box component="li">{t(`${p}.preparacio.darreraRevisio`)}</Box>
                        <Box component="li">{t(`${p}.preparacio.properaRevisio`)}</Box>
                        <Box component="li">{t(`${p}.preparacio.norma`)}</Box>
                    </Box>
                    <Typography component="p" sx={{fontWeight: 600, mt: 1}}>{t(`${p}.preparacio.resultatTitle`)}</Typography>
					<Box component="ul">
						<Box component="li">{t(`${p}.preparacio.puntuacioLabel`)}: {t(`${p}.preparacio.puntuacioVal`)}</Box>
						<Box component="li">{t(`${p}.preparacio.nivellLabel`)}: {t(`${p}.preparacio.nivellVal`)}</Box>
						<Box component="li">{t(`${p}.preparacio.situacioLabel`)}: {t(`${p}.preparacio.situacioVal`)}</Box>
					</Box>
					<Typography component="p">{t(`${p}.preparacio.responsive`)}</Typography>
                </Box>

                {/* Observacions i contacte */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="mail" color="success">{t(`${p}.contacte.title`)}</SectionTitle>
                    <Typography component="p">{t(`${p}.contacte.p1`)}</Typography>
                    <Box component="ul">
                        <Box component="li">{t(`${p}.contacte.li1`)}</Box>
                        <Box component="li">{t(`${p}.contacte.li2`)}</Box>
                        <Box component="li">{t(`${p}.contacte.li3`)}</Box>
                    </Box>
                    <Typography component="p">
                        {t(`${p}.contacte.p2Part1`)}{' '}
                        <Link href="https://www.caib.es/seucaib/es/200/persones/tramites/servicio/4055206/" target="_blank" rel="noopener noreferrer">
                            {t(`${p}.contacte.p2LinkText`)}
                        </Link>
                        {' '}{t(`${p}.contacte.p2Part2`)}
                    </Typography>
                    <Box component="ul">
                        <Box component="li">{t(`${p}.contacte.li4`)}</Box>
                        <Box component="li">
                            {t(`${p}.contacte.li5`)}
                            <Box component="ul">
                                <Box component="li">{t(`${p}.contacte.li5a`)}</Box>
                                <Box component="li">{t(`${p}.contacte.li5b`)}</Box>
                            </Box>
                        </Box>
                    </Box>
                    <Typography component="p">
                        {t(`${p}.contacte.p4Part1`)}{' '}
                        <Link href="https://www.caib.es/seucaib/es/200/personas/tramites/tramite/4055271/" target="_blank" rel="noopener noreferrer">
                            {t(`${p}.contacte.p4LinkText`)}
                        </Link>
                    </Typography>
                </Box>

                {/* Procediment */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="error" color="error">{t(`${p}.procediment.title`)}</SectionTitle>
                    <Typography component="p">{t(`${p}.procediment.p1`)}</Typography>
                    <Typography component="p">{t(`${p}.procediment.p2`)}</Typography>
                    <Typography component="p">
                        {t(`${p}.procediment.p3Part1`)}{' '}
                        <Link href="https://www.caib.es/seucaib/es/200/personas/tramites/tramite/4057494" target="_blank" rel="noopener noreferrer">
                            {t(`${p}.procediment.p3LinkText`)}
                        </Link>
                    </Typography>
                </Box>

                {/* Contingut opcional */}
                <Box sx={{p: 1}}>
                    <SectionTitle icon="add_circle" color="warning">{t(`${p}.opcional.title`)}</SectionTitle>
                    <Box component="ul">
                        <Box component="li">{t(`${p}.opcional.mesures`)}</Box>
                        <Box component="li">{t(`${p}.opcional.config`)}</Box>
                        <Box component="li">{t(`${p}.opcional.recursos`)}</Box>
                    </Box>
                </Box>
            </Box>
        </CardPage>;
}

export default Accesibilitat;
