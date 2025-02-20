import * as React from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import Grid from '@mui/material/Grid2';
import {
    GridPage,
    FormPage,
    MuiGrid,
    MuiForm,
    FormField
} from 'reactlib';

export const AppForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return <FormPage>
        <MuiForm
            id={id}
            title={id ? t('page.apps.update') : t('page.apps.create')}
            resourceName="app"
            goBackLink="/app"
            createLink="form/{{id}}">
            <Grid container spacing={2}>
                <Grid size={4}><FormField name="codi" /></Grid>
                <Grid size={8}></Grid>
                <Grid size={12}><FormField name="nom" /></Grid>
                <Grid size={8}><FormField name="infoUrl" /></Grid>
                <Grid size={4}><FormField name="infoInterval" /></Grid>
                <Grid size={8}><FormField name="salutUrl" /></Grid>
                <Grid size={4}><FormField name="salutInterval" /></Grid>
                <Grid size={12}><FormField name="descripcio" type="textarea" /></Grid>
                <Grid size={12}><FormField name="activa" /></Grid>
            </Grid>
        </MuiForm>
    </FormPage>;
}

const Apps: React.FC = () => {
    const { t } = useTranslation();
    const columns = [{
        field: 'codi',
        flex: 1,
    }, {
        field: 'nom',
        flex: 3,
    }, {
        field: 'activa',
        flex: .5,
    }];
    return <GridPage>
        <MuiGrid
            title={t('page.apps.title')}
            resourceName="app"
            columns={columns}
            toolbarType="upper"
            paginationActive
            //readOnly
            rowDetailLink="/dd"
            toolbarCreateLink="form"
            rowUpdateLink="form/{{id}}"
        />
    </GridPage>;
}

export default Apps;
