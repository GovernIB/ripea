import {MuiFormDialog, useResourceApiService} from "reactlib";
import React, {useState} from "react";
import {Badge, Grid, Icon, IconButton} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {DataFormDialogApi} from "../../../lib/components/mui/datacommon/DataFormDialog.tsx";

const CommentForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="text"/>
    </Grid>
}

const CommentDialog = (props:any) => {
    const { entity } = props;
    const {numComm, handleOpen, dialog} = useCommentDialog(entity);

    return <>
        <IconButton aria-label="forum" color={"inherit"} onClick={handleOpen} /*variant="outlined" sx={{borderRadius: 1}}*/>
            <Badge badgeContent={numComm} color="primary">
                <Icon>forum</Icon>
            </Badge>
        </IconButton>
        {dialog}
    </>
}

const useCommentDialog = (row:any) => {
    const {
        isReady: appApiIsReady,
        find: findAll,
    } = useResourceApiService('expedientComentariResource');
    const [entity] = useState<any>(row);
    const [comentarios, setComentarios] = useState<any[]>();
    const [numComm, setNumComm] = useState<number>(entity?.numComentaris);

    const comment = {borderRadius: 2, px: 2, py: 1}
    const myComment = {...comment, bgcolor: '#a5d6a7', alignSelf: 'end'}
    const otherComment = {...comment, bgcolor: '#e0e0e0'}
    const formApiRef = React.useRef<DataFormDialogApi>()

    const handleOpen = () => {
        if (appApiIsReady){
            findAll({
                filter: `expedient.id:${entity?.id}`,
                includeLinksInRows: true,
                page: 0,
                size: 0,
                sorts: ['createdDate', 'desc']
            })
                .then((app) => {
                    setComentarios(app.rows)
                    setNumComm(app.rows.length)
                })
        }

        formApiRef.current?.show(undefined, {
            expedient: {
                id: entity?.id
            },
        })
            .then(() => {
                setNumComm(numComm + 1);
            })
    }

    const dialog =
        <MuiFormDialog
            resourceName={"expedientComentariResource"}
            title={`Comentarios del expediente: ${entity?.nom}`}
            apiRef={formApiRef}
        >
            <Grid
                container
                direction="column"
                sx={{
                    justifyContent: "center",
                    alignItems: "flex-start",
                    columnSpacing: 1,
                    rowSpacing: 1,
                    pb: 1,
                }}
            >
                {comentarios?.map((a)=>
                    <Grid item key={a?.id} sx={a.createdBy=="rip_admin" ?myComment :otherComment}>
                        {a?.text}
                    </Grid>
                )}
            </Grid>
            <CommentForm/>
        </MuiFormDialog>

    return {
        numComm,
        handleOpen,
        dialog
    }
}
export default CommentDialog;