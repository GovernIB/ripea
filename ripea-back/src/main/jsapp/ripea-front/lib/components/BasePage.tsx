import React from 'react';

export type BasePageProps = React.PropsWithChildren & {
    toolbar?: React.ReactElement;
};

export const BasePage: React.FC<BasePageProps> = (props) => {
    const { toolbar, children } = props;
    return <div>
        {toolbar}
        <div style={{ margin: '16px', marginTop: '24px' }}>
            {children}
        </div>
    </div>;
}

export default BasePage;