import {useEffect, useRef, useState} from "react";
import {
    MuiFormDialogApi,
    useBaseAppContext,
    useFormContext,
} from "reactlib";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import ContingutIcon from "../../contingut/details/ContingutIcon.tsx";
import {FormReportDialog} from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useTranslation} from "react-i18next";
import {GridTreeDataGroupingCell} from "@mui/x-data-grid-pro";
import {iniciaDescargaBlob} from "../details/CommonActions.tsx";
import Load from "../../../components/Load.tsx";
import {useTreeView} from "../../contingut/DocumentsGrid.tsx";

const sortModel:any = [{field: 'id', sort: 'desc'}]
const perspectives = ["PATH"]
const columns = [
    // {
    //     field: 'nom',
    //     flex: 0.5,
    //     renderCell: (params: any) => <ContingutIcon entity={params?.row}/>
    // },
    {
        field: 'descripcio',
        flex: 0.5,
    },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
];

const DescargarDocumentsForm = () => {
    const { t } = useTranslation();
    const {apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [treeView, setTreeView] = useState<boolean>(true);

    useEffect(() => {
        apiRef?.current?.setFieldValue("ids", selectedRows)
    }, [selectedRows]);

    const commonFilter = builder.and(
        builder.or(
            builder.eq('expedient.id', apiRef?.current?.getId()),
            builder.eq('pare.id', apiRef?.current?.getId()),
        ),
        builder.eq('esborrat', 0),
    )

    const {carpetes, expedients, refresh, isReady} = useTreeView(commonFilter)

    return <Load value={apiRef && isReady}>
        <StyledMuiGrid
            resourceName="documentResource"
            columns={columns}
            filter={commonFilter}
            perspectives={perspectives}
            staticSortModel={sortModel}
            onRefresh={refresh}

            groupingColDef={{
                headerName: t('page.contingut.grid.nom'),
                flex: 1.5,
                valueFormatter: (value: any, row: any) => {
                    return row?.id ? <ContingutIcon entity={row} /> : value;
                },
                renderCell: (params: any) => {
                    return treeView
                        ? <GridTreeDataGroupingCell {...params} />
                        : params.formattedValue
                },
            }}

            treeData={true}
            treeDataAdditionalRows={(_rows:any) => {
                const additionalRows :any[] = [];
                for (const contingut of [...carpetes, ...expedients]) {
                    if (apiRef?.current?.getId() != contingut.id && !additionalRows.map((b) => b.id).includes(contingut.id)) {
                        additionalRows.push(contingut)
                    }
                }
                setTreeView(additionalRows?.length > 0)
                // console.log('>>> additionalRows', additionalRows)
                return additionalRows;
            }}
            getTreeDataPath={(row: any): string[] => {
                return row.treePath.filter((id: any) => id != apiRef?.current?.getId());
            }}

            isGroupExpandedByDefault={()=>true}
            isRowSelectable={(data: any) => data?.row?.tipus == "DOCUMENT"}
            readOnly

            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}

            selectionActive
            autoHeight
        />
    </Load>
}

const DescargarDocuments = (props:any) => {
    const {t} = useTranslation();

    return <FormReportDialog
        resourceName={"expedientResource"}
        report={"EXPORT_SELECTED_DOCS"}
        reportFileType={'ZIP'}
        title={t('page.expedient.action.download.title')}
        formDialogComponentProps={{fullWidth: true, maxWidth: 'lg'}}
        {...props}
    >
        <DescargarDocumentsForm/>
    </FormReportDialog>
}

const useDescargarDocuments = () => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = (result:any) :void => {
        iniciaDescargaBlob(result);
        temporalMessageShow(null, t('page.expedient.action.download.ok'), 'success');
    }

    return {
        handleShow,
        content: <DescargarDocuments apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useDescargarDocuments;