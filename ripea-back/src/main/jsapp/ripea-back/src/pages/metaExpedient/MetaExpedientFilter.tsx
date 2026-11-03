import { useMemo } from 'react';
import { useFormContext } from 'reactlib';
import GridFormField, { GridButtonField } from '../../components/GridFormField.tsx';
import StyledMuiFilter from '../../components/StyledMuiFilter.tsx';
import * as builder from '../../util/springFilterUtils.ts';
import { useUserSession } from '../../components/Session.tsx';

const MetaExpedientFilterForm = ({ user }: any) => {
    const { data } = useFormContext();

    const revisioActiva = !!user?.sessionScope?.isRevisioActiva;

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="codi" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
                    {revisioActiva ? (
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="revisioEstat" />
                    ) : (
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="organGestor" disabled={data?.ambit == 'COMUNS'} />
                    )}
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="actiu" />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="codi" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="classificacio" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="ambit" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="organGestor" disabled={data?.ambit == 'COMUNS'} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="revisioEstat" hidden={!revisioActiva} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="actiu" />
                    <GridButtonField
                        size={{ xs: 12, sm: 3, md: 1.5 }}
                        name="permisDirecte"
                        icon={'pan_tool_alt'}
                        whitLabel
                        iconSx={{ transform: 'rotate(180deg)' }}
                    />
                    <GridButtonField size={{ xs: 12, sm: 3, md: 1 }} name="gestioAmbGrupsActiva" icon={'groups'} whitLabel />
                </>
            )}
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
    const { value: user, rol } = useUserSession();

    // El revisor entra al llistat amb l'estat de revisió filtrat a PENDENT, igual que a la
    // UI clàssica (MetaExpedientRevisioController.getFiltreCommand). Només val com a valor
    // inicial: en desar-se el filtre a la sessió, mana el que hagi triat l'usuari.
    const defaultData = useMemo(() => (rol?.isRevisor ? { revisioEstat: 'PENDENT' } : undefined), [rol?.isRevisor]);

    return (
        <StyledMuiFilter
            resourceName={'metaExpedientResource'}
            code={'FILTER_GESTIO'}
            springFilterBuilder={springFilterBuilder}
            defaultData={defaultData}
            advancedSearch
            {...props}
        >
            <MetaExpedientFilterForm user={user} />
        </StyledMuiFilter>
    );
};
