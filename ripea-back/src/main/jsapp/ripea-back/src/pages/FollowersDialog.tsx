import {MuiFormDialog, useResourceApiService} from "reactlib";
import React, {useState} from "react";
import {Badge, Grid, Snackbar, Icon, IconButton} from "@mui/material";
import {useTranslation} from "react-i18next";

const FollowersDialog = (props:any) => {
    const { numFollowers, handleOpen } = props;

    return <IconButton aria-label="forum" color={"inherit"} onClick={handleOpen}>
        <Badge badgeContent={numFollowers} color="primary">
            <Icon>people</Icon>
        </Badge>
    </IconButton>
}

export const ExpedientFollowersDialog = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();
    const {numFollowers, handleOpen, dialog} = useFollowerstDialog({
        row: entity,
        title: `${t('page.expedient.modal.seguidors')}: ${entity?.nom}`,
        resourceName: 'expedientSeguidorResource',
        resourceReference: 'expedient',
    });

    return <>
        <FollowersDialog numFollowers={numFollowers ?? entity?.numSeguidors} handleOpen={handleOpen}/>
        {dialog}
    </>
}

const useFollowerstDialog = (props:any) => {
    const { row, title, resourceName, resourceReference } = props;
    const {
        isReady: appApiIsReady,
        find: findAll,
    } = useResourceApiService(resourceName);
    const [entity] = useState<any>(row);
    const [comentarios, setComentarios] = useState<any[]>();
    const [numFollowers, setnumFollowers] = useState<number>(entity?.numSeguidors);
    const formApiRef = React.useRef<DataFormDialogApi>()

    const handleOpen = () => {
        if (appApiIsReady){
            findAll({
                filter: `${resourceReference}.id:${entity?.id}`,
                includeLinksInRows: true,
                page: 0,
                size: 0,
                sorts: ['seguidor.nom', 'desc']
            })
                .then((app) => {
                    setComentarios(app.rows)
                    setnumFollowers(app.rows.length)
                })
        }

        formApiRef.current?.show(undefined, {
            [resourceReference]: {
                id: entity?.id
            },
        })
            .then(() => {
                setnumFollowers(numFollowers + 1);
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
					<Snackbar message={a?.seguidor.codiAndNom} />
                )}
            </Grid>
        </MuiFormDialog>

    return {
        numFollowers,
        handleOpen,
        dialog
    }
}