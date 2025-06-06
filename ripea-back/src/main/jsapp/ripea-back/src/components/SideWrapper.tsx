interface SideWrapperProps {
    children: React.ReactNode;
    onOutsideClick: () => void;
}

const overlayStyle = {
    position: 'fixed' as const,
    top: 0,
    left: 0,
    width: 'calc(100% - 350px)',
    height: '100%',
    backgroundColor: 'rgba(0,0,0,0.6)',
    zIndex: -1,
};

function SideWrapper ({children, onOutsideClick}: SideWrapperProps) {
    return (
        <>
            <div style={overlayStyle} onClick={onOutsideClick} />
            <div>{children}</div>
        </>
    );
}

export default SideWrapper;
