import { ReactNode } from 'react';
import { Box, Icon, IconButton } from '@mui/material';
import { GridPage } from 'reactlib';
import { DetailCard, DetailCardContent } from './CardData.tsx';
import { ScreenFrame } from './ScreenFrame.tsx';
import { icons } from '../util/icons.ts';
import AlertExpand from './AlertExpand.tsx';
import { useTranslation } from 'react-i18next';

export const ErrorArea = ({ children, ...other }: { children: ReactNode; [key: string]: any }) => {
    return (
        <Box
            {...other}
            sx={{
                border: 'solid 1px',
                borderRadius: '4px',
                display: 'block',
                overflow: 'auto',
                whiteSpace: 'pre',
                fontFamily: 'monospace', // opcional para parecer <pre>
                mt: 1,
                p: 1,
                ...other.sx,
            }}
        >
            {children}
        </Box>
    );
};

export function ErrorPage({ error }: any) {
    console.log('error', error);
    const { t } = useTranslation();
    return (
        <GridPage>
            <ScreenFrame icon={icons.advertencia} title={error.name ?? ''}>
                <DetailCard>
                    <DetailCardContent title={t('common.error.status')}>{error.status}</DetailCardContent>
                    <DetailCardContent title={t('common.error.title')}>{error.description}</DetailCardContent>
                    <DetailCardContent>
                        <AlertExpand
                            label={t('common.error.message')}
                            severity={'error'}
                            action={
                                <>
                                    <IconButton
                                        color="inherit"
                                        size="small"
                                        onClick={() => navigator.clipboard.writeText(error.stackTrace ? error.stackTrace : error.message)}
                                    >
                                        <Icon sx={{ m: 0 }}>content_copy</Icon>
                                    </IconButton>
                                </>
                            }
                        >
                            <ErrorArea
                                sx={{
                                    maxHeight: '500px',
                                }}
                            >
                                {error.stackTrace ? error.stackTrace : error.message}
                            </ErrorArea>
                        </AlertExpand>
                    </DetailCardContent>
                </DetailCard>
            </ScreenFrame>
        </GridPage>
    );
}
