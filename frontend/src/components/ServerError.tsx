interface ServerErrorProps {
    message: string;
}

const ServerError = ({ message }: ServerErrorProps) => (
    <div className="text-sm text-destructive bg-destructive/10 border border-destructive/20 rounded-md px-3 py-2">
        {message}
    </div>
);

export default ServerError;