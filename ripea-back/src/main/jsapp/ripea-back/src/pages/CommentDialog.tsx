import {MuiFormDialog, useResourceApiService} from "reactlib";
import {useEffect, useRef, useState} from "react";
import {Badge, Grid, Icon, IconButton, Typography} from "@mui/material";
import GridFormField from "../components/GridFormField.tsx";
import {DataFormDialogApi} from "../../lib/components/mui/datacommon/DataFormDialog.tsx";
import {formatDate} from "../util/dateUtils.ts";

const CommentForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="text"/>
    </Grid>
}

const comment = {borderRadius: 2, px: 2, py: 1}
const myComment = {...comment, bgcolor: '#a5d6a7', alignSelf: 'end'}
const otherComment = {...comment, bgcolor: '#e0e0e0'}

const Comments = (props:any) => {
    const { entity, resourceName, resourceReference } = props;
    const [comentarios, setComentarios] = useState<any[]>([]);

    const {
        isReady: appApiIsReady,
        find: findAll,
    } = useResourceApiService(resourceName);

    useEffect(() => {
        if (appApiIsReady && comentarios?.length == 0){
            findAll({
                filter: `${resourceReference}.id:${entity?.id}`,
                includeLinksInRows: true,
                unpaged: true,
                sorts: ['createdDate', 'desc']
            })
                .then((app) => {
                    // console.log(">>>> rows", app.rows)
                    setComentarios(app.rows);
                })
        }
    }, [appApiIsReady]);

    return <Grid
        container
        direction="column"
        rowGap={1}
        sx={{
            justifyContent: "center",
            alignItems: "flex-start",
            pb: 1,
        }}
    >
        {comentarios?.map((a:any)=>
            // TODO: check my comment
            <Grid item key={a?.id} sx={a?.createdBy=="rip_admin" ?myComment :otherComment}>
                <Typography variant={"subtitle2"} color={"textDisabled"}>{a?.createdBy}</Typography>
                <Typography variant={"body2"}>{a?.text}</Typography>
                <Typography variant={"caption"} color={"textDisabled"}>{formatDate(a?.createdDate)}</Typography>
            </Grid>
        )}
    </Grid>
}

export const CommentDialog = (props:any) => {
    const { entity, title, resourceName, resourceReference } = props;
    const [numComm, setNumComm] = useState<number>(entity?.numComentaris);
    const formApiRef = useRef<DataFormDialogApi>()

    const handleOpen = () => {
        formApiRef.current?.show(undefined, {
            [resourceReference]: {
                id: entity?.id
            },
        })
            .then(() => {
                setNumComm((numComm ?? entity?.numComentaris) + 1);
            })
    }

    return <>
        <IconButton aria-label="forum" color={"inherit"} onClick={handleOpen}>
            <Badge badgeContent={numComm ?? entity?.numComentaris} color="primary">
                <Icon>forum</Icon>
            </Badge>
        </IconButton>

        <MuiFormDialog
            resourceName={resourceName}
            title={title}
            apiRef={formApiRef}
        >
            <Comments
                entity={entity}
                resourceName={resourceName}
                resourceReference={resourceReference}
            />
            <CommentForm/>
        </MuiFormDialog>
    </>
}