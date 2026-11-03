import GridFormField from '../../../../components/GridFormField.tsx';
import * as builder from '../../../../util/springFilterUtils.ts';
import StyledMuiFilter from '../../../../components/StyledMuiFilter.tsx';
import { useFormContext } from 'reactlib';

const IntegracioFilterForm = () => {
    const { data } = useFormContext();

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="entitat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="descripcio" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.25 }} name="estat" />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="entitat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataFi" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="descripcio" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq('entitat.id', data?.entitat?.id), //Al back (additionalSpringFilter) es substitueix per entitatCodi
        builder.betweenDates('data', data?.dataInici, data?.dataFi),
        builder.eq('tipus', `'${data?.tipus}'`),
        builder.like('descripcio', data?.descripcio),
        builder.eq('estat', `'${data?.estat}'`)
    );
};

export const IntegracioFilter = (props: any) => {
    const { onSpringFilterChange } = props;
    return (
        <StyledMuiFilter
            resourceName={'integracioResource'}
            code={'FILTER'}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={onSpringFilterChange}
            advancedSearch
        >
            <IntegracioFilterForm />
        </StyledMuiFilter>
    );
};
