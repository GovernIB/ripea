import {useResourceApiService} from "reactlib";
import {useEffect, useState} from "react";
import {Badge, Icon, IconButton, Typography} from "@mui/material";
import Dialog from "../../lib/components/mui/Dialog.tsx";
import {useTranslation} from 'react-i18next';

const followerStyle = {
	borderRadius: 2,
	px: 2,
	py: 1,
	bgcolor: '#e0e0e0',
	color: 'rgba(0, 0, 0, 0.38)'}

const Follower = (props:any) => {
	
    const { entity } = props;
    const [comentarios, setComentarios] = useState<any[]>([]);
	const resourceName = 'expedientSeguidorResource';
	const resourceReference = 'expedient';
	
    const {
        isReady: appApiIsReady,
        find: findAll,
    } = useResourceApiService(resourceName);

    useEffect(() => {
        if (appApiIsReady && comentarios?.length == 0){
            findAll({
                filter: `${resourceReference}.id:${entity?.id}`,
                includeLinksInRows: true,
                page: 0,
                size: 0,
                sorts: ['seguidor.nom', 'desc']
            })
                .then((app) => {
                    setComentarios(app.rows);
					//TODO: actualizar numComm
                })
        }
    }, [appApiIsReady]);

    return <>
        {comentarios?.map((a:any)=><>
			<Typography sx={followerStyle} color="primary">{a?.seguidor?.description}</Typography>
       	</>
	   )}
    </>
}

export const FollowersDialog = (props:any) => {

	const { entity } = props;
    const [numComm] = useState<number>(entity?.numSeguidors);
	const [open, setOpen] = useState(false);
	const { t } = useTranslation();
	const title = t('page.expedient.modal.seguidors') +': '+ entity?.nom;
	
    const handleOpen = () => {
		setOpen(true);
    }

	const handleClose = () => { setOpen(false);	};
	
    return <>
	
		<IconButton aria-label="forum" color={"inherit"} onClick={handleOpen}>
	        <Badge badgeContent={numComm} color="primary">
	            <Icon>people</Icon>
	        </Badge>
	    </IconButton>	
	
		<Dialog
		    open={open}
			closeCallback={handleClose}
			title={title}
			key={entity?.id}
		    componentProps={{ fullWidth: true, maxWidth: 'md', height: '100%', }}
		    buttons={[
		        {
		            value: 'close',
		            text: t('common.close'),
		            icon: 'close'
		        },
		    ]}
		    buttonCallback={(value :any) :void=>{
		        if (value=='close') {
		            handleClose();
		        }
		    }}>
            <Follower entity={entity} />
        </Dialog>
    </>
}