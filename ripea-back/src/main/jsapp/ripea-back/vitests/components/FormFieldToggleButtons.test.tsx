import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FormFieldToggleButtons from '@src/components/FormFieldToggleButtons';

const requestHref = vi.fn();

vi.mock('reactlib', () => ({
    useResourceApiContext: () => ({ requestHref }),
}));

const fieldAmbOptions = {
    options: { CREAR: 'Crear expedient', INCORPORAR: 'Incorporar a expedient existent' },
};

const renderField = (props: any = {}) =>
    render(
        <FormFieldToggleButtons
            name="accio"
            label="Acció"
            value={props.value}
            field={props.field ?? fieldAmbOptions}
            onChange={props.onChange ?? vi.fn()}
            {...props}
        />
    );

describe('FormFieldToggleButtons', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('renderitza un botó per cada opció del camp', () => {
        renderField();
        expect(screen.getByRole('button', { name: 'Crear expedient' })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'Incorporar a expedient existent' })).toBeInTheDocument();
    });

    it('marca com a seleccionat el botó del valor actual', () => {
        renderField({ value: 'INCORPORAR' });
        expect(screen.getByRole('button', { name: 'Crear expedient' }))
            .toHaveAttribute('aria-pressed', 'false');
        expect(screen.getByRole('button', { name: 'Incorporar a expedient existent' }))
            .toHaveAttribute('aria-pressed', 'true');
    });

    it('notifica el valor en triar una altra opció', async () => {
        const onChange = vi.fn();
        renderField({ value: 'CREAR', onChange });
        await userEvent.click(screen.getByRole('button', { name: 'Incorporar a expedient existent' }));
        expect(onChange).toHaveBeenCalledWith('INCORPORAR');
    });

    it('no permet deseleccionar el valor si el camp és obligatori', async () => {
        const onChange = vi.fn();
        renderField({ value: 'CREAR', onChange, required: true });
        await userEvent.click(screen.getByRole('button', { name: 'Crear expedient' }));
        expect(onChange).not.toHaveBeenCalled();
    });

    it('buida el valor en deseleccionar si el camp no és obligatori', async () => {
        const onChange = vi.fn();
        renderField({ value: 'CREAR', onChange });
        await userEvent.click(screen.getByRole('button', { name: 'Crear expedient' }));
        expect(onChange).toHaveBeenCalledWith(undefined);
    });

    it('carrega les opcions del dataSource quan el camp no les porta', async () => {
        requestHref.mockResolvedValue({
            getEmbedded: () => [
                { data: { id: 'CREAR', description: 'Crear expedient' } },
                { data: { id: 'INCORPORAR', description: 'Incorporar a expedient existent' } },
            ],
        });
        renderField({
            field: {
                dataSource: { href: '/api/enum/accio', valueField: 'id', labelField: 'description' },
            },
        });
        await waitFor(() =>
            expect(screen.getByRole('button', { name: 'Crear expedient' })).toBeInTheDocument()
        );
        expect(requestHref).toHaveBeenCalledWith('/api/enum/accio', undefined);
    });

    it('no mostra els valors indicats a hiddenEnumValues', () => {
        renderField({ value: 'CREAR', hiddenEnumValues: ['INCORPORAR'] });
        expect(screen.getByRole('button', { name: 'Crear expedient' })).toBeInTheDocument();
        expect(screen.queryByRole('button', { name: 'Incorporar a expedient existent' })).toBeNull();
    });

    it('mostra el missatge d\'error del camp com a text d\'ajuda', () => {
        renderField({ fieldError: { field: 'accio', message: 'El camp és obligatori' } });
        expect(screen.getByText('El camp és obligatori')).toBeInTheDocument();
    });
});
