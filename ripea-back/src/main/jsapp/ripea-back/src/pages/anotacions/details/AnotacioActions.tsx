import useAnotacioDetail from "./AnotacioDetail.tsx";

const useAnotacioActions = (refresh?: () => void) => {

    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            title: "Detalle",
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
    ];

    const components = <>
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;