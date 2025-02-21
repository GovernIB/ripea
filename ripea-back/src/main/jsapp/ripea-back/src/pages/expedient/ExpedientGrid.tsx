import React, {useState} from 'react';
import {
    GridPage,
    MuiGrid,
    MuiFilter,
    FormField,
    useFilterApiRef
} from 'reactlib';
import Button from "@mui/material/Button";

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();
    const [springFilter, setSpringFilter] = useState("");
    const filterRef = useFilterApiRef();

    const columns = [
        {
            field: 'codi',
            flex: 0.5,
        },
        {
            field: 'nom',
            flex: 2,
        },
        {
            field: 'tipusStr',
            flex: 1.5,
        },
    ];

    const and = (...options :any[]) :string => {
        return options.filter(a=>a!=null).join(" AND ");
    }
    const like = (option :string, value :any) :string | null => {
        return value ?`${option}~'%${value}%'` :null;
    }

    const springFilterBuilder = (data: any) => {
        let filterStr :string = '';

        filterStr += and(
            like("codi", data.codi),
            like("nom", data.nom),
        )
        console.log('>>> springFilterBuilder:', filterStr)
        return filterStr;
    }
    const cercar = ()=> {
        filterRef.current.filter()
    }
    const netejar = ()=> {
        filterRef.current.clear()
    }
    return <GridPage>
        <MuiFilter
            resourceName="expedientResource"
            code="EXPEDIENT_FILTER"
            springFilterBuilder={springFilterBuilder}
            commonFieldComponentProps={{ size: 'small' }}
            componentProps={{
                sx: { mb: 2 }
            }}
            apiRef={filterRef}
            onSpringFilterChange={setSpringFilter}
            buttonControlled
        >
            <FormField name="codi" />
            <FormField name="nom" />

            <Button onClick={netejar}>Netejar</Button>
            <Button onClick={cercar} variant="contained">Cercar</Button>
        </MuiFilter>
        <MuiGrid
            resourceName="expedientResource"
            columns={columns}
            paginationActive
            filter={springFilter} />
    </GridPage>;
}

export default ExpedientGrid;