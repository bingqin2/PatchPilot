interface RecordLineProps {
  title: string;
  meta: string;
  body: string;
}

export function RecordLine({ title, meta, body }: RecordLineProps) {
  return (
    <div className="record-line">
      <div>
        <strong>{title}</strong>
        <span>{meta}</span>
      </div>
      <p>{body}</p>
    </div>
  );
}
