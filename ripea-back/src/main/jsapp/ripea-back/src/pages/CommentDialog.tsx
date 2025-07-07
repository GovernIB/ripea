import {useEffect, useRef, useState} from "react";
import {Badge, Grid, Icon, IconButton, Typography} from "@mui/material";
import {MuiFormDialog, useResourceApiService, MuiFormDialogApi} from "reactlib";
import GridFormField from "../components/GridFormField.tsx";
import {formatDate} from "../util/dateUtils.ts";
import {useUserSession} from "../components/Session.tsx";
import Load from "../components/Load.tsx";

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

    const { value: user } = useUserSession();
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
            <Grid item key={a?.id} sx={a?.createdBy==user?.codi ?myComment :otherComment}>
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
    const formApiRef = useRef<MuiFormDialogApi>()

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
        <Load value={entity} noEffect>
            <MuiFormDialog
                resourceName={resourceName}
                title={title}
                onClose={(reason?: string) => reason !== 'backdropClick'}
                apiRef={formApiRef}
            >
                <Comments
                    entity={entity}
                    resourceName={resourceName}
                    resourceReference={resourceReference}
                />
                <CommentForm/>
            </MuiFormDialog>
        </Load>
    </>
}