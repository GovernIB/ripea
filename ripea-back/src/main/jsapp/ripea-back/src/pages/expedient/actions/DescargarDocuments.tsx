import {useEffect, useRef, useState} from "react";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import ContingutIcon from "../../contingut/details/ContingutIcon.tsx";
import {FormReportDialog} from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useTranslation} from "react-i18next";

const sortModel:any = [{field: 'id', sort: 'desc'}]
const perspectives = ["PATH"]
const columns = [
    {
        field: 'nom',
        flex: 0.5,
        renderCell: (params: any) => <ContingutIcon entity={params?.row}/>
    },
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
    const {apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    useEffect(() => {
        apiRef?.current?.setFieldValue("ids", selectedRows)
    }, [selectedRows]);

    return <StyledMuiGrid
        resourceName="documentResource"
        columns={columns}
        filter={builder.and(
            builder.eq('expedient.id', apiRef?.current?.getId()),
            builder.eq('esborrat', 0),
        )}
        perspectives={perspectives}
        staticSortModel={sortModel}
        treeData={true}
        treeDataAdditionalRows={(_rows:any) => {
            const additionalRows :any[] = [];
            if(_rows!=null) {
                for (const row of _rows) {
                    const aditionalRow = row.parentPath
                        ?.filter((a: any) => a.id != row.id
                            && !additionalRows.map((b) => b.id).includes(a.id)
                            && !additionalRows.map((b) => b.nom).includes(a.nom))
                    aditionalRow && additionalRows.push(...aditionalRow);
                }
            }
            // console.log('>>> additionalRows', additionalRows)
            return additionalRows;
        }}
        getTreeDataPath={(row:any) :string[] => row.treePath }

        isGroupExpandedByDefault={()=>true}
        isRowSelectable={(data:any)=> data?.row?.id}
        readOnly

        onRowSelectionModelChange={(newSelection) => {
            setSelectedRows([...newSelection]);
        }}

        selectionActive
        autoHeight
    />
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

const useDescargarDocuments = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.download.ok'), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <DescargarDocuments apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useDescargarDocuments;