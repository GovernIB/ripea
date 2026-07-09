import { useFormContext } from 'reactlib';
import GridFormField, { GridButtonField } from '../../components/GridFormField.tsx';
import StyledMuiFilter from '../../components/StyledMuiFilter.tsx';
import * as builder from '../../util/springFilterUtils.ts';
import { useUserSession } from '../../components/Session.tsx';

const MetaExpedientFilterForm = ({ user }: any) => {
    const { data } = useFormContext();

    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 2 }} name="tipus" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 2 }} name="codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 3 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 3 }} name="classificacio" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 2 }} name="revisioEstat" hidden={!user?.sessionScope?.isRevisioActiva} />
            <GridFormField size={{ xs: 12, sm: 6, md: 2, lg: 1.5 }} name="actiu" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3, lg: 2 }} name="ambit" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4, lg: 3 }} name="organGestor" disabled={data?.ambit == 'COMUNS'} />
            <GridButtonField
                size={{ xs: 12, sm: 3, md: 3, lg: 1.5 }}
                name="permisDirecte"
                icon={'pan_tool_alt'}
                whitLabel
                iconSx={{ transform: 'rotate(180deg)' }}
            />
            <GridButtonField size={{ xs: 12, sm: 3, md: 3, lg: 1.5 }} name="gestioAmbGrupsActiva" icon={'groups'} whitLabel />
        </>
    );
};

export const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.like('classificacio', data?.classificacio),
        builder.eq('organGestor.id', data?.organGestor?.id),
        data?.actiu && builder.eq('actiu', data?.actiu),
        data?.revisioEstat && builder.eq('revisioEstat', `'${data?.revisioEstat}'`),
        data?.tipus && builder.eq('tipusProcedimentServei', `'${data?.tipus}'`),
        data?.permisDirecte && builder.eq('permisDirecte', data?.permisDirecte),
        data?.gestioAmbGrupsActiva && builder.eq('gestioAmbGrupsActiva', data?.gestioAmbGrupsActiva),
        data?.ambit && builder.equals('organGestor.id', null, data?.ambit == 'COMUNS')
    );
};

export const MetaExpedientFilter = (props: any) => {
    const { value: user } = useUserSession();

    return (
        <StyledMuiFilter resourceName={'metaExpedientResource'} code={'FILTER_GESTIO'} springFilterBuilder={springFilterBuilder} {...props}>
            <MetaExpedientFilterForm user={user} />
        </StyledMuiFilter>
    );
};
