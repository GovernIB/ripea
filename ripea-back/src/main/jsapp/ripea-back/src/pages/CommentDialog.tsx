import {MuiFormDialog, useResourceApiService} from "reactlib";
import React, {useState} from "react";
import {Badge, Grid, Icon, IconButton} from "@mui/material";
import GridFormField from "../components/GridFormField.tsx";
import {DataFormDialogApi} from "../../lib/components/mui/datacommon/DataFormDialog.tsx";
import {useTranslation} from "react-i18next";

const CommentForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="text"/>
    </Grid>
}

const CommentDialog = (props:any) => {
    const { numComm, handleOpen } = props;

    return <IconButton aria-label="forum" color={"inherit"} onClick={handleOpen}>
        <Badge badgeContent={numComm} color="primary">
            <Icon>forum</Icon>
        </Badge>
    </IconButton>
}

export const ExpedientCommentDialog = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();
    const {numComm, handleOpen, dialog} = useCommentDialog({
        row: entity,
        title: `${t('page.comment.expedient')}: ${entity?.nom}`,
        resourceName: 'expedientComentariResource',
        resourceReference: 'expedient',
    });

    return <>
        <CommentDialog numComm={numComm} handleOpen={handleOpen}/>
        {dialog}
    </>
}

export const TascaCommentDialog = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();
    const {numComm, handleOpen, dialog} = useCommentDialog({
        row: entity,
        title: `${t('page.comment.tasca')}: ${entity?.titol}`,
        resourceName: 'expedientTascaComentariResource',
        resourceReference: 'expedientTasca',
    });

    return <>
        <CommentDialog numComm={numComm} handleOpen={handleOpen}/>
        {dialog}
    </>
}

const useCommentDialog = (props:any) => {
    const { row, title, resourceName, resourceReference } = props;
    const {
        isReady: appApiIsReady,
        find: findAll,
    } = useResourceApiService(resourceName);
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
                filter: `${resourceReference}.id:${entity?.id}`,
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
            [resourceReference]: {
                id: entity?.id
            },
        })
            .then(() => {
                setNumComm(numComm + 1);
            })
    }

    const dialog =
        <MuiFormDialog
            resourceName={resourceName}
            title={title}
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