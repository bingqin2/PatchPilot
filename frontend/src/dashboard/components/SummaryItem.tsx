interface SummaryItemProps {
  label: string;
  value: string | number;
}

export function SummaryItem({ label, value }: SummaryItemProps) {
  return (
    <div>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
