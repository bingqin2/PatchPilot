import { Archive, Copy, Download, ExternalLink } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoAcceptanceSummary,
  DemoFinalAcceptanceCompletionArchive,
  DemoFinalAcceptanceCompletionCloseoutArchive,
  DemoFinalAcceptanceCompletionCloseout,
  DemoFinalAcceptanceCompletionEvidenceBundle,
  DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput,
  DemoFinalExternalReviewEvidencePackageArchive,
  DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive,
  DemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  DemoFinalExternalReviewEvidencePackageDeliveryReceipt,
  DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput,
  DemoFinalExternalReviewDeliveryCertificateArchive,
  DemoFinalExternalReviewDeliveryCertificate,
  DemoFinalExternalReviewReleaseBundle,
  DemoFinalExternalReviewReleaseBundleArchive,
  DemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive,
  DemoFinalExternalReviewReleaseBundleDeliveryReceipt,
  DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput,
  DemoFinalExternalReviewEvidencePackage,
  DemoFinalAcceptanceShareDeliveryReceipt,
  DemoFinalAcceptanceShareDeliveryReceiptInput,
  DemoFinalAcceptanceShareFinalization,
  DemoFinalAcceptanceSharePackage,
  DemoFinalAcceptanceSharePackageArchive,
  DemoReadinessStatus
} from '../../types';
import { compactDateTime } from '../format';

interface DemoAcceptanceSummaryPanelProps {
  summary: DemoAcceptanceSummary | null;
  sharePackage: DemoFinalAcceptanceSharePackage | null;
  sharePackageArchives: DemoFinalAcceptanceSharePackageArchive[];
  shareDeliveryReceipts: DemoFinalAcceptanceShareDeliveryReceipt[];
  shareFinalization: DemoFinalAcceptanceShareFinalization | null;
  completionEvidenceBundle: DemoFinalAcceptanceCompletionEvidenceBundle | null;
  completionArchives: DemoFinalAcceptanceCompletionArchive[];
  completionCloseoutArchives?: DemoFinalAcceptanceCompletionCloseoutArchive[];
  completionEvidenceDeliveryReceipts: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[];
  completionEvidenceDeliveryFinalization?: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization | null;
  completionCloseout?: DemoFinalAcceptanceCompletionCloseout | null;
  finalExternalReviewEvidencePackage?: DemoFinalExternalReviewEvidencePackage | null;
  finalExternalReviewEvidencePackageArchives?: DemoFinalExternalReviewEvidencePackageArchive[];
  finalExternalReviewEvidencePackageDeliveryReceipts?: DemoFinalExternalReviewEvidencePackageDeliveryReceipt[];
  finalExternalReviewEvidencePackageDeliveryFinalization?:
    DemoFinalExternalReviewEvidencePackageDeliveryFinalization | null;
  finalExternalReviewEvidencePackageDeliveryFinalizationArchives?:
    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive[];
  finalExternalReviewDeliveryCertificate?: DemoFinalExternalReviewDeliveryCertificate | null;
  finalExternalReviewDeliveryCertificateArchives?: DemoFinalExternalReviewDeliveryCertificateArchive[];
  finalExternalReviewReleaseBundle?: DemoFinalExternalReviewReleaseBundle | null;
  finalExternalReviewReleaseBundleArchives?: DemoFinalExternalReviewReleaseBundleArchive[];
  finalExternalReviewReleaseBundleDeliveryReceipts?: DemoFinalExternalReviewReleaseBundleDeliveryReceipt[];
  finalExternalReviewReleaseBundleDeliveryFinalization?:
    DemoFinalExternalReviewReleaseBundleDeliveryFinalization | null;
  finalExternalReviewReleaseBundleDeliveryFinalizationArchives?:
    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive[];
  error: string | null;
  sharePackageError: string | null;
  sharePackageArchiveError: string | null;
  shareDeliveryReceiptError: string | null;
  shareFinalizationError: string | null;
  completionEvidenceBundleError: string | null;
  completionArchiveError: string | null;
  completionCloseoutArchiveError?: string | null;
  completionEvidenceDeliveryReceiptError: string | null;
  completionEvidenceDeliveryFinalizationError?: string | null;
  completionCloseoutError?: string | null;
  finalExternalReviewEvidencePackageError?: string | null;
  finalExternalReviewEvidencePackageArchiveError?: string | null;
  finalExternalReviewEvidencePackageDeliveryReceiptError?: string | null;
  finalExternalReviewEvidencePackageDeliveryFinalizationError?: string | null;
  finalExternalReviewEvidencePackageDeliveryFinalizationArchiveError?: string | null;
  finalExternalReviewDeliveryCertificateError?: string | null;
  finalExternalReviewDeliveryCertificateArchiveError?: string | null;
  finalExternalReviewReleaseBundleError?: string | null;
  finalExternalReviewReleaseBundleArchiveError?: string | null;
  finalExternalReviewReleaseBundleDeliveryReceiptError?: string | null;
  finalExternalReviewReleaseBundleDeliveryFinalizationError?: string | null;
  finalExternalReviewReleaseBundleDeliveryFinalizationArchiveError?: string | null;
  onDownloadReport: () => Promise<Blob>;
  onDownloadSharePackageReport: () => Promise<Blob>;
  onArchiveSharePackage: () => Promise<DemoFinalAcceptanceSharePackageArchive>;
  onDownloadSharePackageArchiveReport: (archiveId: string) => Promise<Blob>;
  onCreateShareDeliveryReceipt: (
    input: DemoFinalAcceptanceShareDeliveryReceiptInput
  ) => Promise<DemoFinalAcceptanceShareDeliveryReceipt>;
  onDownloadShareDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onDownloadShareFinalizationReport: () => Promise<Blob>;
  onDownloadCompletionEvidenceBundleReport: () => Promise<Blob>;
  onArchiveCompletion: () => Promise<DemoFinalAcceptanceCompletionArchive>;
  onDownloadCompletionArchiveReport: (archiveId: string) => Promise<Blob>;
  onArchiveCompletionCloseout?: () => Promise<DemoFinalAcceptanceCompletionCloseoutArchive>;
  onDownloadCompletionCloseoutArchiveReport?: (archiveId: string) => Promise<Blob>;
  onCreateCompletionEvidenceDeliveryReceipt: (
    input: DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput
  ) => Promise<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt>;
  onDownloadCompletionEvidenceDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onDownloadCompletionEvidenceDeliveryFinalizationReport?: () => Promise<Blob>;
  onDownloadCompletionCloseoutReport?: () => Promise<Blob>;
  onDownloadFinalExternalReviewEvidencePackageReport?: () => Promise<Blob>;
  onArchiveFinalExternalReviewEvidencePackage?: () => Promise<DemoFinalExternalReviewEvidencePackageArchive>;
  onDownloadFinalExternalReviewEvidencePackageArchiveReport?: (archiveId: string) => Promise<Blob>;
  onCreateFinalExternalReviewEvidencePackageDeliveryReceipt?: (
    input: DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput
  ) => Promise<DemoFinalExternalReviewEvidencePackageDeliveryReceipt>;
  onDownloadFinalExternalReviewEvidencePackageDeliveryReceiptReport?: (receiptId: string) => Promise<Blob>;
  onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationReport?: () => Promise<Blob>;
  onArchiveFinalExternalReviewEvidencePackageDeliveryFinalization?: () => Promise<
    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive
  >;
  onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport?: (
    archiveId: string
  ) => Promise<Blob>;
  onDownloadFinalExternalReviewDeliveryCertificateReport?: () => Promise<Blob>;
  onArchiveFinalExternalReviewDeliveryCertificate?: () => Promise<
    DemoFinalExternalReviewDeliveryCertificateArchive
  >;
  onDownloadFinalExternalReviewDeliveryCertificateArchiveReport?: (archiveId: string) => Promise<Blob>;
  onDownloadFinalExternalReviewReleaseBundleReport?: () => Promise<Blob>;
  onArchiveFinalExternalReviewReleaseBundle?: () => Promise<DemoFinalExternalReviewReleaseBundleArchive>;
  onDownloadFinalExternalReviewReleaseBundleArchiveReport?: (archiveId: string) => Promise<Blob>;
  onCreateFinalExternalReviewReleaseBundleDeliveryReceipt?: (
    input: DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput
  ) => Promise<DemoFinalExternalReviewReleaseBundleDeliveryReceipt>;
  onDownloadFinalExternalReviewReleaseBundleDeliveryReceiptReport?: (receiptId: string) => Promise<Blob>;
  onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationReport?: () => Promise<Blob>;
  onArchiveFinalExternalReviewReleaseBundleDeliveryFinalization?: () => Promise<
    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive
  >;
  onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport?: (
    archiveId: string
  ) => Promise<Blob>;
}

export function DemoAcceptanceSummaryPanel({
  summary,
  sharePackage,
  sharePackageArchives,
  shareDeliveryReceipts,
  shareFinalization,
  completionEvidenceBundle,
  completionArchives,
  completionCloseoutArchives = [],
  completionEvidenceDeliveryReceipts,
  completionEvidenceDeliveryFinalization = null,
  completionCloseout = null,
  finalExternalReviewEvidencePackage = null,
  finalExternalReviewEvidencePackageArchives = [],
  finalExternalReviewEvidencePackageDeliveryReceipts = [],
  finalExternalReviewEvidencePackageDeliveryFinalization = null,
  finalExternalReviewEvidencePackageDeliveryFinalizationArchives = [],
  finalExternalReviewDeliveryCertificate = null,
  finalExternalReviewDeliveryCertificateArchives = [],
  finalExternalReviewReleaseBundle = null,
  finalExternalReviewReleaseBundleArchives = [],
  finalExternalReviewReleaseBundleDeliveryReceipts = [],
  finalExternalReviewReleaseBundleDeliveryFinalization = null,
  finalExternalReviewReleaseBundleDeliveryFinalizationArchives = [],
  error,
  sharePackageError,
  sharePackageArchiveError,
  shareDeliveryReceiptError,
  shareFinalizationError,
  completionEvidenceBundleError,
  completionArchiveError,
  completionCloseoutArchiveError = null,
  completionEvidenceDeliveryReceiptError,
  completionEvidenceDeliveryFinalizationError = null,
  completionCloseoutError = null,
  finalExternalReviewEvidencePackageError = null,
  finalExternalReviewEvidencePackageArchiveError = null,
  finalExternalReviewEvidencePackageDeliveryReceiptError = null,
  finalExternalReviewEvidencePackageDeliveryFinalizationError = null,
  finalExternalReviewEvidencePackageDeliveryFinalizationArchiveError = null,
  finalExternalReviewDeliveryCertificateError = null,
  finalExternalReviewDeliveryCertificateArchiveError = null,
  finalExternalReviewReleaseBundleError = null,
  finalExternalReviewReleaseBundleArchiveError = null,
  finalExternalReviewReleaseBundleDeliveryReceiptError = null,
  finalExternalReviewReleaseBundleDeliveryFinalizationError = null,
  finalExternalReviewReleaseBundleDeliveryFinalizationArchiveError = null,
  onDownloadReport,
  onDownloadSharePackageReport,
  onArchiveSharePackage,
  onDownloadSharePackageArchiveReport,
  onCreateShareDeliveryReceipt,
  onDownloadShareDeliveryReceiptReport,
  onDownloadShareFinalizationReport,
  onDownloadCompletionEvidenceBundleReport,
  onArchiveCompletion,
  onDownloadCompletionArchiveReport,
  onArchiveCompletionCloseout,
  onDownloadCompletionCloseoutArchiveReport,
  onCreateCompletionEvidenceDeliveryReceipt,
  onDownloadCompletionEvidenceDeliveryReceiptReport,
  onDownloadCompletionEvidenceDeliveryFinalizationReport,
  onDownloadCompletionCloseoutReport,
  onDownloadFinalExternalReviewEvidencePackageReport,
  onArchiveFinalExternalReviewEvidencePackage,
  onDownloadFinalExternalReviewEvidencePackageArchiveReport,
  onCreateFinalExternalReviewEvidencePackageDeliveryReceipt,
  onDownloadFinalExternalReviewEvidencePackageDeliveryReceiptReport,
  onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationReport,
  onArchiveFinalExternalReviewEvidencePackageDeliveryFinalization,
  onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport,
  onDownloadFinalExternalReviewDeliveryCertificateReport,
  onArchiveFinalExternalReviewDeliveryCertificate,
  onDownloadFinalExternalReviewDeliveryCertificateArchiveReport,
  onDownloadFinalExternalReviewReleaseBundleReport,
  onArchiveFinalExternalReviewReleaseBundle,
  onDownloadFinalExternalReviewReleaseBundleArchiveReport,
  onCreateFinalExternalReviewReleaseBundleDeliveryReceipt,
  onDownloadFinalExternalReviewReleaseBundleDeliveryReceiptReport,
  onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationReport,
  onArchiveFinalExternalReviewReleaseBundleDeliveryFinalization,
  onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport
}: DemoAcceptanceSummaryPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [sharePackageCopyStatus, setSharePackageCopyStatus] = useState<string | null>(null);
  const [sharePackageDownloadStatus, setSharePackageDownloadStatus] = useState<string | null>(null);
  const [sharePackageArchiveStatus, setSharePackageArchiveStatus] = useState<string | null>(null);
  const [sharePackageArchiveDownloadStatus, setSharePackageArchiveDownloadStatus] = useState<string | null>(null);
  const [shareDeliveryReceiptStatus, setShareDeliveryReceiptStatus] = useState<string | null>(null);
  const [shareFinalizationDownloadStatus, setShareFinalizationDownloadStatus] = useState<string | null>(null);
  const [completionEvidenceBundleDownloadStatus, setCompletionEvidenceBundleDownloadStatus] = useState<string | null>(null);
  const [completionArchiveStatus, setCompletionArchiveStatus] = useState<string | null>(null);
  const [completionArchiveDownloadStatus, setCompletionArchiveDownloadStatus] = useState<string | null>(null);
  const [completionCloseoutArchiveStatus, setCompletionCloseoutArchiveStatus] = useState<string | null>(null);
  const [
    completionCloseoutArchiveDownloadStatus,
    setCompletionCloseoutArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [completionEvidenceDeliveryReceiptStatus, setCompletionEvidenceDeliveryReceiptStatus] = useState<string | null>(null);
  const [
    completionEvidenceDeliveryFinalizationDownloadStatus,
    setCompletionEvidenceDeliveryFinalizationDownloadStatus
  ] = useState<string | null>(null);
  const [completionCloseoutDownloadStatus, setCompletionCloseoutDownloadStatus] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDownloadStatus,
    setFinalExternalReviewEvidencePackageDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageArchiveStatus,
    setFinalExternalReviewEvidencePackageArchiveStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageArchiveDownloadStatus,
    setFinalExternalReviewEvidencePackageArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDeliveryReceiptStatus,
    setFinalExternalReviewEvidencePackageDeliveryReceiptStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDeliveryReceiptDownloadStatus,
    setFinalExternalReviewEvidencePackageDeliveryReceiptDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDeliveryFinalizationDownloadStatus,
    setFinalExternalReviewEvidencePackageDeliveryFinalizationDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveStatus,
    setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveDownloadStatus,
    setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewDeliveryCertificateDownloadStatus,
    setFinalExternalReviewDeliveryCertificateDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewDeliveryCertificateArchiveStatus,
    setFinalExternalReviewDeliveryCertificateArchiveStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewDeliveryCertificateArchiveDownloadStatus,
    setFinalExternalReviewDeliveryCertificateArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDownloadStatus,
    setFinalExternalReviewReleaseBundleDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleArchiveStatus,
    setFinalExternalReviewReleaseBundleArchiveStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleArchiveDownloadStatus,
    setFinalExternalReviewReleaseBundleArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDeliveryReceiptStatus,
    setFinalExternalReviewReleaseBundleDeliveryReceiptStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDeliveryReceiptDownloadStatus,
    setFinalExternalReviewReleaseBundleDeliveryReceiptDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDeliveryFinalizationDownloadStatus,
    setFinalExternalReviewReleaseBundleDeliveryFinalizationDownloadStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDeliveryFinalizationArchiveStatus,
    setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveStatus
  ] = useState<string | null>(null);
  const [
    finalExternalReviewReleaseBundleDeliveryFinalizationArchiveDownloadStatus,
    setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState('');
  const [operator, setOperator] = useState('');
  const [deliveryNotes, setDeliveryNotes] = useState('');
  const [completionEvidenceDeliveryChannel, setCompletionEvidenceDeliveryChannel] = useState('email');
  const [completionEvidenceDeliveryTarget, setCompletionEvidenceDeliveryTarget] = useState('');
  const [completionEvidenceOperator, setCompletionEvidenceOperator] = useState('');
  const [completionEvidenceDeliveryNotes, setCompletionEvidenceDeliveryNotes] = useState('');
  const [finalExternalReviewPackageDeliveryChannel, setFinalExternalReviewPackageDeliveryChannel] = useState('email');
  const [finalExternalReviewPackageDeliveryTarget, setFinalExternalReviewPackageDeliveryTarget] = useState('');
  const [finalExternalReviewPackageDeliveryOperator, setFinalExternalReviewPackageDeliveryOperator] = useState('');
  const [finalExternalReviewPackageDeliveryNotes, setFinalExternalReviewPackageDeliveryNotes] = useState('');
  const [finalExternalReviewReleaseBundleDeliveryChannel, setFinalExternalReviewReleaseBundleDeliveryChannel] = useState('email');
  const [finalExternalReviewReleaseBundleDeliveryTarget, setFinalExternalReviewReleaseBundleDeliveryTarget] = useState('');
  const [finalExternalReviewReleaseBundleDeliveryOperator, setFinalExternalReviewReleaseBundleDeliveryOperator] = useState('');
  const [finalExternalReviewReleaseBundleDeliveryNotes, setFinalExternalReviewReleaseBundleDeliveryNotes] = useState('');

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-summary.md');
      setDownloadStatus('Final demo acceptance report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copySharePackage() {
    if (!sharePackage) {
      return;
    }
    try {
      await navigator.clipboard.writeText(formatSharePackageClipboard(sharePackage));
      setSharePackageCopyStatus('Final acceptance share package copied');
    } catch {
      setSharePackageCopyStatus('Copy failed');
    }
  }

  async function downloadSharePackage() {
    try {
      const report = await onDownloadSharePackageReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-share-package.md');
      setSharePackageDownloadStatus('Final acceptance share package downloaded');
    } catch {
      setSharePackageDownloadStatus('Download failed');
    }
  }

  async function archiveSharePackage() {
    try {
      await onArchiveSharePackage();
      setSharePackageArchiveStatus('Final acceptance share package archived');
    } catch {
      setSharePackageArchiveStatus('Archive failed');
    }
  }

  async function downloadSharePackageArchive(archive: DemoFinalAcceptanceSharePackageArchive) {
    try {
      const report = await onDownloadSharePackageArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-demo-acceptance-share-package-${archive.id}.md`);
      setSharePackageArchiveDownloadStatus('Archived final acceptance package downloaded');
    } catch {
      setSharePackageArchiveDownloadStatus('Archive download failed');
    }
  }

  async function createShareDeliveryReceipt() {
    try {
      await onCreateShareDeliveryReceipt({
        deliveryChannel,
        deliveryTarget: deliveryTarget.trim(),
        operator: operator.trim(),
        notes: deliveryNotes.trim()
      });
      setShareDeliveryReceiptStatus('Final acceptance delivery receipt recorded');
    } catch {
      setShareDeliveryReceiptStatus('Delivery receipt failed');
    }
  }

  async function downloadShareFinalization() {
    try {
      const report = await onDownloadShareFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-share-finalization.md');
      setShareFinalizationDownloadStatus('Final acceptance finalization report downloaded');
    } catch {
      setShareFinalizationDownloadStatus('Finalization download failed');
    }
  }

  async function downloadCompletionEvidenceBundle() {
    try {
      const report = await onDownloadCompletionEvidenceBundleReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-evidence-bundle.md');
      setCompletionEvidenceBundleDownloadStatus('Final acceptance completion evidence bundle downloaded');
    } catch {
      setCompletionEvidenceBundleDownloadStatus('Final acceptance completion evidence bundle download failed');
    }
  }

  async function downloadCompletionEvidenceDeliveryFinalization() {
    if (!onDownloadCompletionEvidenceDeliveryFinalizationReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionEvidenceDeliveryFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-evidence-delivery-finalization.md');
      setCompletionEvidenceDeliveryFinalizationDownloadStatus(
        'Final acceptance completion delivery finalization report downloaded'
      );
    } catch {
      setCompletionEvidenceDeliveryFinalizationDownloadStatus(
        'Final acceptance completion delivery finalization report download failed'
      );
    }
  }

  async function downloadCompletionCloseout() {
    if (!onDownloadCompletionCloseoutReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionCloseoutReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-closeout.md');
      setCompletionCloseoutDownloadStatus('Final acceptance completion closeout report downloaded');
    } catch {
      setCompletionCloseoutDownloadStatus('Final acceptance completion closeout report download failed');
    }
  }

  async function downloadFinalExternalReviewEvidencePackage() {
    if (!onDownloadFinalExternalReviewEvidencePackageReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewEvidencePackageReport();
      downloadMarkdown(report, 'patchpilot-final-external-review-evidence-package.md');
      setFinalExternalReviewEvidencePackageDownloadStatus('Final external-review evidence package downloaded');
    } catch {
      setFinalExternalReviewEvidencePackageDownloadStatus('Final external-review evidence package download failed');
    }
  }

  async function archiveFinalExternalReviewEvidencePackage() {
    if (!onArchiveFinalExternalReviewEvidencePackage) {
      return;
    }
    try {
      await onArchiveFinalExternalReviewEvidencePackage();
      setFinalExternalReviewEvidencePackageArchiveStatus('Final external-review evidence package archived');
    } catch {
      setFinalExternalReviewEvidencePackageArchiveStatus('Final external-review evidence package archive failed');
    }
  }

  async function downloadFinalExternalReviewEvidencePackageArchive(
    archive: DemoFinalExternalReviewEvidencePackageArchive
  ) {
    if (!onDownloadFinalExternalReviewEvidencePackageArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewEvidencePackageArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-external-review-evidence-package-${archive.id}.md`);
      setFinalExternalReviewEvidencePackageArchiveDownloadStatus(
        'Final external-review evidence package archive downloaded'
      );
    } catch {
      setFinalExternalReviewEvidencePackageArchiveDownloadStatus(
        'Final external-review evidence package archive download failed'
      );
    }
  }

  async function createFinalExternalReviewEvidencePackageDeliveryReceipt() {
    if (!onCreateFinalExternalReviewEvidencePackageDeliveryReceipt) {
      return;
    }
    try {
      await onCreateFinalExternalReviewEvidencePackageDeliveryReceipt({
        deliveryChannel: finalExternalReviewPackageDeliveryChannel,
        deliveryTarget: finalExternalReviewPackageDeliveryTarget.trim(),
        operator: finalExternalReviewPackageDeliveryOperator.trim(),
        notes: finalExternalReviewPackageDeliveryNotes.trim()
      });
      setFinalExternalReviewEvidencePackageDeliveryReceiptStatus(
        'Final external-review package delivery receipt recorded'
      );
    } catch {
      setFinalExternalReviewEvidencePackageDeliveryReceiptStatus(
        'Final external-review package delivery receipt failed'
      );
    }
  }

  async function downloadFinalExternalReviewEvidencePackageDeliveryReceipt(
    receipt: DemoFinalExternalReviewEvidencePackageDeliveryReceipt
  ) {
    if (!onDownloadFinalExternalReviewEvidencePackageDeliveryReceiptReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewEvidencePackageDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-final-external-review-package-delivery-receipt-${receipt.id}.md`);
      setFinalExternalReviewEvidencePackageDeliveryReceiptDownloadStatus(
        'Final external-review package delivery receipt downloaded'
      );
    } catch {
      setFinalExternalReviewEvidencePackageDeliveryReceiptDownloadStatus(
        'Final external-review package delivery receipt download failed'
      );
    }
  }

  async function downloadFinalExternalReviewEvidencePackageDeliveryFinalization() {
    if (!onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-external-review-package-delivery-finalization.md');
      setFinalExternalReviewEvidencePackageDeliveryFinalizationDownloadStatus(
        'Final external-review package delivery finalization downloaded'
      );
    } catch {
      setFinalExternalReviewEvidencePackageDeliveryFinalizationDownloadStatus(
        'Final external-review package delivery finalization report download failed'
      );
    }
  }

  async function archiveFinalExternalReviewEvidencePackageDeliveryFinalization() {
    if (!onArchiveFinalExternalReviewEvidencePackageDeliveryFinalization) {
      return;
    }
    try {
      await onArchiveFinalExternalReviewEvidencePackageDeliveryFinalization();
      setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveStatus(
        'Final external-review package delivery finalization archived'
      );
    } catch {
      setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveStatus(
        'Final external-review package delivery finalization archive failed'
      );
    }
  }

  async function downloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchive(
    archive: DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive
  ) {
    if (!onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport(archive.id);
      downloadMarkdown(
        report,
        `patchpilot-final-external-review-package-delivery-finalization-${archive.id}.md`
      );
      setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveDownloadStatus(
        'Final external-review package delivery finalization archive downloaded'
      );
    } catch {
      setFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveDownloadStatus(
        'Final external-review package delivery finalization archive download failed'
      );
    }
  }

  async function downloadFinalExternalReviewDeliveryCertificate() {
    if (!onDownloadFinalExternalReviewDeliveryCertificateReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewDeliveryCertificateReport();
      downloadMarkdown(report, 'patchpilot-final-external-review-delivery-certificate.md');
      setFinalExternalReviewDeliveryCertificateDownloadStatus(
        'Final external-review delivery certificate downloaded'
      );
    } catch {
      setFinalExternalReviewDeliveryCertificateDownloadStatus(
        'Final external-review delivery certificate download failed'
      );
    }
  }

  async function archiveFinalExternalReviewDeliveryCertificate() {
    if (!onArchiveFinalExternalReviewDeliveryCertificate) {
      return;
    }
    try {
      await onArchiveFinalExternalReviewDeliveryCertificate();
      setFinalExternalReviewDeliveryCertificateArchiveStatus(
        'Final external-review delivery certificate archived'
      );
    } catch {
      setFinalExternalReviewDeliveryCertificateArchiveStatus(
        'Final external-review delivery certificate archive failed'
      );
    }
  }

  async function downloadFinalExternalReviewDeliveryCertificateArchive(
    archive: DemoFinalExternalReviewDeliveryCertificateArchive
  ) {
    if (!onDownloadFinalExternalReviewDeliveryCertificateArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewDeliveryCertificateArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-external-review-delivery-certificate-${archive.id}.md`);
      setFinalExternalReviewDeliveryCertificateArchiveDownloadStatus(
        'Final external-review delivery certificate archive downloaded'
      );
    } catch {
      setFinalExternalReviewDeliveryCertificateArchiveDownloadStatus(
        'Final external-review delivery certificate archive download failed'
      );
    }
  }

  async function downloadFinalExternalReviewReleaseBundle() {
    if (!onDownloadFinalExternalReviewReleaseBundleReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewReleaseBundleReport();
      downloadMarkdown(report, 'patchpilot-final-external-review-release-bundle.md');
      setFinalExternalReviewReleaseBundleDownloadStatus(
        'Final external-review release bundle downloaded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDownloadStatus(
        'Final external-review release bundle download failed'
      );
    }
  }

  async function archiveFinalExternalReviewReleaseBundle() {
    if (!onArchiveFinalExternalReviewReleaseBundle) {
      return;
    }
    try {
      await onArchiveFinalExternalReviewReleaseBundle();
      setFinalExternalReviewReleaseBundleArchiveStatus('Final external-review release bundle archived');
    } catch {
      setFinalExternalReviewReleaseBundleArchiveStatus(
        'Final external-review release bundle archive failed'
      );
    }
  }

  async function downloadFinalExternalReviewReleaseBundleArchive(
    archive: DemoFinalExternalReviewReleaseBundleArchive
  ) {
    if (!onDownloadFinalExternalReviewReleaseBundleArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewReleaseBundleArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-external-review-release-bundle-${archive.id}.md`);
      setFinalExternalReviewReleaseBundleArchiveDownloadStatus(
        'Final external-review release bundle archive downloaded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleArchiveDownloadStatus(
        'Final external-review release bundle archive download failed'
      );
    }
  }

  async function createFinalExternalReviewReleaseBundleDeliveryReceipt() {
    if (!onCreateFinalExternalReviewReleaseBundleDeliveryReceipt) {
      return;
    }
    try {
      await onCreateFinalExternalReviewReleaseBundleDeliveryReceipt({
        deliveryChannel: finalExternalReviewReleaseBundleDeliveryChannel,
        deliveryTarget: finalExternalReviewReleaseBundleDeliveryTarget.trim(),
        operator: finalExternalReviewReleaseBundleDeliveryOperator.trim(),
        notes: finalExternalReviewReleaseBundleDeliveryNotes.trim()
      });
      setFinalExternalReviewReleaseBundleDeliveryReceiptStatus(
        'Final external-review release bundle delivery receipt recorded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDeliveryReceiptStatus(
        'Final external-review release bundle delivery receipt failed'
      );
    }
  }

  async function downloadFinalExternalReviewReleaseBundleDeliveryReceipt(
    receipt: DemoFinalExternalReviewReleaseBundleDeliveryReceipt
  ) {
    if (!onDownloadFinalExternalReviewReleaseBundleDeliveryReceiptReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewReleaseBundleDeliveryReceiptReport(receipt.id);
      downloadMarkdown(
        report,
        `patchpilot-final-external-review-release-bundle-delivery-receipt-${receipt.id}.md`
      );
      setFinalExternalReviewReleaseBundleDeliveryReceiptDownloadStatus(
        'Final external-review release bundle delivery receipt downloaded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDeliveryReceiptDownloadStatus(
        'Final external-review release bundle delivery receipt download failed'
      );
    }
  }

  async function downloadFinalExternalReviewReleaseBundleDeliveryFinalization() {
    if (!onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-external-review-release-bundle-delivery-finalization.md');
      setFinalExternalReviewReleaseBundleDeliveryFinalizationDownloadStatus(
        'Final external-review release bundle delivery finalization downloaded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDeliveryFinalizationDownloadStatus(
        'Final external-review release bundle delivery finalization download failed'
      );
    }
  }

  async function archiveFinalExternalReviewReleaseBundleDeliveryFinalization() {
    if (!onArchiveFinalExternalReviewReleaseBundleDeliveryFinalization) {
      return;
    }
    try {
      await onArchiveFinalExternalReviewReleaseBundleDeliveryFinalization();
      setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveStatus(
        'Final external-review release bundle delivery finalization archived'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveStatus(
        'Final external-review release bundle delivery finalization archive failed'
      );
    }
  }

  async function downloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchive(
    archive: DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive
  ) {
    if (!onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport(
        archive.id
      );
      downloadMarkdown(
        report,
        `patchpilot-final-external-review-release-bundle-delivery-finalization-${archive.id}.md`
      );
      setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveDownloadStatus(
        'Final external-review release bundle delivery finalization archive downloaded'
      );
    } catch {
      setFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveDownloadStatus(
        'Final external-review release bundle delivery finalization archive download failed'
      );
    }
  }

  async function archiveCompletionCloseout() {
    if (!onArchiveCompletionCloseout) {
      return;
    }
    try {
      await onArchiveCompletionCloseout();
      setCompletionCloseoutArchiveStatus('Final acceptance completion closeout archived');
    } catch {
      setCompletionCloseoutArchiveStatus('Final acceptance completion closeout archive failed');
    }
  }

  async function downloadCompletionCloseoutArchive(archive: DemoFinalAcceptanceCompletionCloseoutArchive) {
    if (!onDownloadCompletionCloseoutArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionCloseoutArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-acceptance-completion-closeout-${archive.id}.md`);
      setCompletionCloseoutArchiveDownloadStatus('Final acceptance completion closeout archive downloaded');
    } catch {
      setCompletionCloseoutArchiveDownloadStatus('Final acceptance completion closeout archive download failed');
    }
  }

  async function downloadShareDeliveryReceipt(receipt: DemoFinalAcceptanceShareDeliveryReceipt) {
    try {
      const report = await onDownloadShareDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-final-demo-acceptance-share-delivery-receipt-${receipt.id}.md`);
      setShareDeliveryReceiptStatus(`Final acceptance delivery receipt ${receipt.id} downloaded`);
    } catch {
      setShareDeliveryReceiptStatus('Delivery receipt download failed');
    }
  }

  async function archiveCompletion() {
    try {
      await onArchiveCompletion();
      setCompletionArchiveStatus('Final acceptance completion archived');
    } catch {
      setCompletionArchiveStatus('Final acceptance completion archive failed');
    }
  }

  async function downloadCompletionArchive(archive: DemoFinalAcceptanceCompletionArchive) {
    try {
      const report = await onDownloadCompletionArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-acceptance-completion-${archive.id}.md`);
      setCompletionArchiveDownloadStatus('Final acceptance completion archive downloaded');
    } catch {
      setCompletionArchiveDownloadStatus('Final acceptance completion archive download failed');
    }
  }

  async function createCompletionEvidenceDeliveryReceipt() {
    try {
      await onCreateCompletionEvidenceDeliveryReceipt({
        deliveryChannel: completionEvidenceDeliveryChannel,
        deliveryTarget: completionEvidenceDeliveryTarget.trim(),
        operator: completionEvidenceOperator.trim(),
        notes: completionEvidenceDeliveryNotes.trim()
      });
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt recorded');
    } catch {
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt failed');
    }
  }

  async function downloadCompletionEvidenceDeliveryReceipt(
    receipt: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt
  ) {
    try {
      const report = await onDownloadCompletionEvidenceDeliveryReceiptReport(receipt.id);
      downloadMarkdown(
        report,
        `patchpilot-final-acceptance-completion-evidence-delivery-receipt-${receipt.id}.md`
      );
      setCompletionEvidenceDeliveryReceiptStatus(
        `Final acceptance completion evidence delivery receipt ${receipt.id} downloaded`
      );
    } catch {
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt download failed');
    }
  }

  return (
    <section className="panel demo-acceptance-summary-panel" aria-label="Final demo acceptance">
      <div className="panel-header">
        <div>
          <h2>Final demo acceptance</h2>
          <p>{summary?.summary ?? 'Loading final demo acceptance summary'}</p>
        </div>
        <div className="demo-evidence-header-actions">
          {summary ? (
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(summary.status)}`}>
              {statusLabel(summary.status)}
            </span>
          ) : null}
          <button className="secondary-button" type="button" onClick={() => void downloadReport()}>
            <Download size={14} />
            Download final acceptance report
          </button>
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Final demo acceptance unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {summary ? (
        <>
          <div className="demo-evidence-grid">
            <AcceptanceStat
              label="Acceptance"
              value={summary.accepted ? 'Accepted' : 'Not accepted'}
              detail={summary.nextAction}
            />
            <AcceptanceStat
              label="Launch certificate"
              value={summary.launchCertificateCertified ? 'Certified' : statusLabel(summary.launchCertificateStatus)}
              detail={summary.launchCertificateArchiveId ?? 'No launch certificate archive'}
            />
            <AcceptanceStat
              label="Task evidence certificate"
              value={summary.taskCertificateCertified ? 'Certified' : statusLabel(summary.taskCertificateStatus)}
              detail={summary.taskCertificateArchiveId ?? 'No task certificate archive'}
            />
            <AcceptanceStat
              label="Generated"
              value={compactDateTime(summary.generatedAt)}
              detail="Read-only summary"
            />
          </div>

          <div className="demo-evidence-records">
            <CertificateEvidence
              title="Launch acceptance certificate"
              status={summary.launchCertificateStatus}
              archived={summary.launchCertificateArchived}
              certified={summary.launchCertificateCertified}
              archiveId={summary.launchCertificateArchiveId}
              closeoutArchiveId={summary.launchCloseoutArchiveId}
              evidenceArchiveId={summary.launchEvidenceArchiveId}
              deliveryReceiptId={summary.launchDeliveryReceiptId}
            />
            <CertificateEvidence
              title="Task evidence acceptance certificate"
              status={summary.taskCertificateStatus}
              archived={summary.taskCertificateArchived}
              certified={summary.taskCertificateCertified}
              archiveId={summary.taskCertificateArchiveId}
              closeoutArchiveId={summary.taskCloseoutArchiveId}
              evidenceArchiveId={summary.taskEvidenceArchiveId}
              deliveryReceiptId={summary.taskDeliveryReceiptId}
            />
            <div>
              <span>Latest task</span>
              <strong>{summary.latestTaskId ?? 'No certified task'}</strong>
              {summary.latestPullRequestUrl ? (
                <a href={summary.latestPullRequestUrl} target="_blank" rel="noreferrer">
                  <ExternalLink size={13} />
                  Open Pull Request
                </a>
              ) : (
                <small>No certified Pull Request</small>
              )}
            </div>
            <div>
              <span>Read-only contract</span>
              <strong>No side effects</strong>
              <small>{summary.sideEffectContract}</small>
            </div>
          </div>

          <div className="demo-webhook-delivery-trail">
            <div className="section-heading">
              <h3>Acceptance checks</h3>
              <span>{summary.checks.length} checks</span>
            </div>
            {summary.checks.length === 0 ? (
              <p>No acceptance checks recorded.</p>
            ) : (
              <ul>
                {summary.checks.map((check) => (
                  <li key={check.name}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{check.name}</strong>
                      <span>{statusLabel(check.status)}</span>
                    </div>
                    <p>{check.summary}</p>
                    <small>{check.nextAction}</small>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="demo-evidence-actions">
            <h3>Evidence notes</h3>
            <ul>
              {summary.evidenceNotes.map((note) => (
                <li key={note}>{note}</li>
              ))}
            </ul>
          </div>

          <div className="demo-evidence-actions">
            <h3>Download actions</h3>
            <ul>
              {summary.downloadActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>

          <FinalAcceptanceSharePackage
            sharePackage={sharePackage}
            archives={sharePackageArchives}
            deliveryReceipts={shareDeliveryReceipts}
            finalization={shareFinalization}
            completionEvidenceBundle={completionEvidenceBundle}
            completionEvidenceDeliveryFinalization={completionEvidenceDeliveryFinalization}
            completionCloseout={completionCloseout}
            completionEvidenceDeliveryReceipts={completionEvidenceDeliveryReceipts}
            error={sharePackageError}
            archiveError={sharePackageArchiveError}
            deliveryReceiptError={shareDeliveryReceiptError}
            finalizationError={shareFinalizationError}
            completionEvidenceBundleError={completionEvidenceBundleError}
            completionEvidenceDeliveryFinalizationError={completionEvidenceDeliveryFinalizationError}
            completionCloseoutError={completionCloseoutError}
            copyStatus={sharePackageCopyStatus}
            downloadStatus={sharePackageDownloadStatus}
            archiveStatus={sharePackageArchiveStatus}
            archiveDownloadStatus={sharePackageArchiveDownloadStatus}
            deliveryReceiptStatus={shareDeliveryReceiptStatus}
            finalizationDownloadStatus={shareFinalizationDownloadStatus}
            completionEvidenceBundleDownloadStatus={completionEvidenceBundleDownloadStatus}
            completionEvidenceDeliveryFinalizationDownloadStatus={
              completionEvidenceDeliveryFinalizationDownloadStatus
            }
            completionCloseoutDownloadStatus={completionCloseoutDownloadStatus}
            completionCloseoutArchives={completionCloseoutArchives}
            completionArchives={completionArchives}
            completionArchiveError={completionArchiveError}
            completionCloseoutArchiveError={completionCloseoutArchiveError}
            completionEvidenceDeliveryReceiptError={completionEvidenceDeliveryReceiptError}
            completionArchiveStatus={completionArchiveStatus}
            completionArchiveDownloadStatus={completionArchiveDownloadStatus}
            completionCloseoutArchiveStatus={completionCloseoutArchiveStatus}
            completionCloseoutArchiveDownloadStatus={completionCloseoutArchiveDownloadStatus}
            completionEvidenceDeliveryReceiptStatus={completionEvidenceDeliveryReceiptStatus}
            deliveryChannel={deliveryChannel}
            deliveryTarget={deliveryTarget}
            operator={operator}
            deliveryNotes={deliveryNotes}
            completionEvidenceDeliveryChannel={completionEvidenceDeliveryChannel}
            completionEvidenceDeliveryTarget={completionEvidenceDeliveryTarget}
            completionEvidenceOperator={completionEvidenceOperator}
            completionEvidenceDeliveryNotes={completionEvidenceDeliveryNotes}
            onCopy={() => void copySharePackage()}
            onDownload={() => void downloadSharePackage()}
            onArchive={() => void archiveSharePackage()}
            onDownloadArchive={(archive) => void downloadSharePackageArchive(archive)}
            onDeliveryChannelChange={setDeliveryChannel}
            onDeliveryTargetChange={setDeliveryTarget}
            onOperatorChange={setOperator}
            onDeliveryNotesChange={setDeliveryNotes}
            onCompletionEvidenceDeliveryChannelChange={setCompletionEvidenceDeliveryChannel}
            onCompletionEvidenceDeliveryTargetChange={setCompletionEvidenceDeliveryTarget}
            onCompletionEvidenceOperatorChange={setCompletionEvidenceOperator}
            onCompletionEvidenceDeliveryNotesChange={setCompletionEvidenceDeliveryNotes}
            onCreateDeliveryReceipt={() => void createShareDeliveryReceipt()}
            onDownloadDeliveryReceipt={(receipt) => void downloadShareDeliveryReceipt(receipt)}
            onDownloadFinalization={() => void downloadShareFinalization()}
            onDownloadCompletionEvidenceBundle={() => void downloadCompletionEvidenceBundle()}
            onDownloadCompletionEvidenceDeliveryFinalization={() => void downloadCompletionEvidenceDeliveryFinalization()}
            onDownloadCompletionCloseout={() => void downloadCompletionCloseout()}
            onArchiveCompletion={() => void archiveCompletion()}
            onDownloadCompletionArchive={(archive) => void downloadCompletionArchive(archive)}
            onArchiveCompletionCloseout={() => void archiveCompletionCloseout()}
            onDownloadCompletionCloseoutArchive={(archive) => void downloadCompletionCloseoutArchive(archive)}
            onCreateCompletionEvidenceDeliveryReceipt={() => void createCompletionEvidenceDeliveryReceipt()}
            onDownloadCompletionEvidenceDeliveryReceipt={(receipt) => void downloadCompletionEvidenceDeliveryReceipt(receipt)}
          />

          <FinalExternalReviewEvidencePackage
            evidencePackage={finalExternalReviewEvidencePackage}
            archives={finalExternalReviewEvidencePackageArchives}
            deliveryReceipts={finalExternalReviewEvidencePackageDeliveryReceipts}
            deliveryFinalization={finalExternalReviewEvidencePackageDeliveryFinalization}
            deliveryFinalizationArchives={finalExternalReviewEvidencePackageDeliveryFinalizationArchives}
            deliveryCertificate={finalExternalReviewDeliveryCertificate}
            deliveryCertificateArchives={finalExternalReviewDeliveryCertificateArchives}
            releaseBundle={finalExternalReviewReleaseBundle}
            releaseBundleArchives={finalExternalReviewReleaseBundleArchives}
            releaseBundleDeliveryReceipts={finalExternalReviewReleaseBundleDeliveryReceipts}
            releaseBundleDeliveryFinalization={finalExternalReviewReleaseBundleDeliveryFinalization}
            releaseBundleDeliveryFinalizationArchives={
              finalExternalReviewReleaseBundleDeliveryFinalizationArchives
            }
            error={finalExternalReviewEvidencePackageError}
            archiveError={finalExternalReviewEvidencePackageArchiveError}
            deliveryReceiptError={finalExternalReviewEvidencePackageDeliveryReceiptError}
            deliveryFinalizationError={finalExternalReviewEvidencePackageDeliveryFinalizationError}
            deliveryFinalizationArchiveError={finalExternalReviewEvidencePackageDeliveryFinalizationArchiveError}
            deliveryCertificateError={finalExternalReviewDeliveryCertificateError}
            deliveryCertificateArchiveError={finalExternalReviewDeliveryCertificateArchiveError}
            releaseBundleError={finalExternalReviewReleaseBundleError}
            releaseBundleArchiveError={finalExternalReviewReleaseBundleArchiveError}
            releaseBundleDeliveryReceiptError={finalExternalReviewReleaseBundleDeliveryReceiptError}
            releaseBundleDeliveryFinalizationError={finalExternalReviewReleaseBundleDeliveryFinalizationError}
            releaseBundleDeliveryFinalizationArchiveError={
              finalExternalReviewReleaseBundleDeliveryFinalizationArchiveError
            }
            downloadStatus={finalExternalReviewEvidencePackageDownloadStatus}
            archiveStatus={finalExternalReviewEvidencePackageArchiveStatus}
            archiveDownloadStatus={finalExternalReviewEvidencePackageArchiveDownloadStatus}
            deliveryReceiptStatus={finalExternalReviewEvidencePackageDeliveryReceiptStatus}
            deliveryReceiptDownloadStatus={finalExternalReviewEvidencePackageDeliveryReceiptDownloadStatus}
            deliveryFinalizationDownloadStatus={finalExternalReviewEvidencePackageDeliveryFinalizationDownloadStatus}
            deliveryFinalizationArchiveStatus={finalExternalReviewEvidencePackageDeliveryFinalizationArchiveStatus}
            deliveryFinalizationArchiveDownloadStatus={
              finalExternalReviewEvidencePackageDeliveryFinalizationArchiveDownloadStatus
            }
            deliveryCertificateDownloadStatus={finalExternalReviewDeliveryCertificateDownloadStatus}
            deliveryCertificateArchiveStatus={finalExternalReviewDeliveryCertificateArchiveStatus}
            deliveryCertificateArchiveDownloadStatus={
              finalExternalReviewDeliveryCertificateArchiveDownloadStatus
            }
            releaseBundleDownloadStatus={finalExternalReviewReleaseBundleDownloadStatus}
            releaseBundleArchiveStatus={finalExternalReviewReleaseBundleArchiveStatus}
            releaseBundleArchiveDownloadStatus={finalExternalReviewReleaseBundleArchiveDownloadStatus}
            releaseBundleDeliveryReceiptStatus={finalExternalReviewReleaseBundleDeliveryReceiptStatus}
            releaseBundleDeliveryReceiptDownloadStatus={
              finalExternalReviewReleaseBundleDeliveryReceiptDownloadStatus
            }
            releaseBundleDeliveryFinalizationDownloadStatus={
              finalExternalReviewReleaseBundleDeliveryFinalizationDownloadStatus
            }
            releaseBundleDeliveryFinalizationArchiveStatus={
              finalExternalReviewReleaseBundleDeliveryFinalizationArchiveStatus
            }
            releaseBundleDeliveryFinalizationArchiveDownloadStatus={
              finalExternalReviewReleaseBundleDeliveryFinalizationArchiveDownloadStatus
            }
            deliveryChannel={finalExternalReviewPackageDeliveryChannel}
            deliveryTarget={finalExternalReviewPackageDeliveryTarget}
            operator={finalExternalReviewPackageDeliveryOperator}
            deliveryNotes={finalExternalReviewPackageDeliveryNotes}
            releaseBundleDeliveryChannel={finalExternalReviewReleaseBundleDeliveryChannel}
            releaseBundleDeliveryTarget={finalExternalReviewReleaseBundleDeliveryTarget}
            releaseBundleDeliveryOperator={finalExternalReviewReleaseBundleDeliveryOperator}
            releaseBundleDeliveryNotes={finalExternalReviewReleaseBundleDeliveryNotes}
            onDownload={() => void downloadFinalExternalReviewEvidencePackage()}
            onArchive={() => void archiveFinalExternalReviewEvidencePackage()}
            onDownloadArchive={(archive) => void downloadFinalExternalReviewEvidencePackageArchive(archive)}
            onDeliveryChannelChange={setFinalExternalReviewPackageDeliveryChannel}
            onDeliveryTargetChange={setFinalExternalReviewPackageDeliveryTarget}
            onOperatorChange={setFinalExternalReviewPackageDeliveryOperator}
            onDeliveryNotesChange={setFinalExternalReviewPackageDeliveryNotes}
            onCreateDeliveryReceipt={() => void createFinalExternalReviewEvidencePackageDeliveryReceipt()}
            onDownloadDeliveryReceipt={(receipt) => void downloadFinalExternalReviewEvidencePackageDeliveryReceipt(receipt)}
            onDownloadDeliveryFinalization={() => void downloadFinalExternalReviewEvidencePackageDeliveryFinalization()}
            onArchiveDeliveryFinalization={() => void archiveFinalExternalReviewEvidencePackageDeliveryFinalization()}
            onDownloadDeliveryFinalizationArchive={(archive) => (
              void downloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchive(archive)
            )}
            onDownloadDeliveryCertificate={() => void downloadFinalExternalReviewDeliveryCertificate()}
            onArchiveDeliveryCertificate={() => void archiveFinalExternalReviewDeliveryCertificate()}
            onDownloadDeliveryCertificateArchive={(archive) => (
              void downloadFinalExternalReviewDeliveryCertificateArchive(archive)
            )}
            onDownloadReleaseBundle={() => void downloadFinalExternalReviewReleaseBundle()}
            onArchiveReleaseBundle={() => void archiveFinalExternalReviewReleaseBundle()}
            onDownloadReleaseBundleArchive={(archive) => (
              void downloadFinalExternalReviewReleaseBundleArchive(archive)
            )}
            onReleaseBundleDeliveryChannelChange={setFinalExternalReviewReleaseBundleDeliveryChannel}
            onReleaseBundleDeliveryTargetChange={setFinalExternalReviewReleaseBundleDeliveryTarget}
            onReleaseBundleDeliveryOperatorChange={setFinalExternalReviewReleaseBundleDeliveryOperator}
            onReleaseBundleDeliveryNotesChange={setFinalExternalReviewReleaseBundleDeliveryNotes}
            onCreateReleaseBundleDeliveryReceipt={() => (
              void createFinalExternalReviewReleaseBundleDeliveryReceipt()
            )}
            onDownloadReleaseBundleDeliveryReceipt={(receipt) => (
              void downloadFinalExternalReviewReleaseBundleDeliveryReceipt(receipt)
            )}
            onDownloadReleaseBundleDeliveryFinalization={() => (
              void downloadFinalExternalReviewReleaseBundleDeliveryFinalization()
            )}
            onArchiveReleaseBundleDeliveryFinalization={() => (
              void archiveFinalExternalReviewReleaseBundleDeliveryFinalization()
            )}
            onDownloadReleaseBundleDeliveryFinalizationArchive={(archive) => (
              void downloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchive(archive)
            )}
          />
        </>
      ) : (
        <div className="empty-state">Final demo acceptance summary has not loaded yet.</div>
      )}
    </section>
  );
}

interface FinalExternalReviewEvidencePackageProps {
  evidencePackage: DemoFinalExternalReviewEvidencePackage | null;
  archives: DemoFinalExternalReviewEvidencePackageArchive[];
  deliveryReceipts: DemoFinalExternalReviewEvidencePackageDeliveryReceipt[];
  deliveryFinalization: DemoFinalExternalReviewEvidencePackageDeliveryFinalization | null;
  deliveryFinalizationArchives: DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive[];
  deliveryCertificate: DemoFinalExternalReviewDeliveryCertificate | null;
  deliveryCertificateArchives: DemoFinalExternalReviewDeliveryCertificateArchive[];
  releaseBundle: DemoFinalExternalReviewReleaseBundle | null;
  releaseBundleArchives: DemoFinalExternalReviewReleaseBundleArchive[];
  releaseBundleDeliveryReceipts: DemoFinalExternalReviewReleaseBundleDeliveryReceipt[];
  releaseBundleDeliveryFinalization: DemoFinalExternalReviewReleaseBundleDeliveryFinalization | null;
  releaseBundleDeliveryFinalizationArchives: DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive[];
  error: string | null;
  archiveError: string | null;
  deliveryReceiptError: string | null;
  deliveryFinalizationError: string | null;
  deliveryFinalizationArchiveError: string | null;
  deliveryCertificateError: string | null;
  deliveryCertificateArchiveError: string | null;
  releaseBundleError: string | null;
  releaseBundleArchiveError: string | null;
  releaseBundleDeliveryReceiptError: string | null;
  releaseBundleDeliveryFinalizationError: string | null;
  releaseBundleDeliveryFinalizationArchiveError: string | null;
  downloadStatus: string | null;
  archiveStatus: string | null;
  archiveDownloadStatus: string | null;
  deliveryReceiptStatus: string | null;
  deliveryReceiptDownloadStatus: string | null;
  deliveryFinalizationDownloadStatus: string | null;
  deliveryFinalizationArchiveStatus: string | null;
  deliveryFinalizationArchiveDownloadStatus: string | null;
  deliveryCertificateDownloadStatus: string | null;
  deliveryCertificateArchiveStatus: string | null;
  deliveryCertificateArchiveDownloadStatus: string | null;
  releaseBundleDownloadStatus: string | null;
  releaseBundleArchiveStatus: string | null;
  releaseBundleArchiveDownloadStatus: string | null;
  releaseBundleDeliveryReceiptStatus: string | null;
  releaseBundleDeliveryReceiptDownloadStatus: string | null;
  releaseBundleDeliveryFinalizationDownloadStatus: string | null;
  releaseBundleDeliveryFinalizationArchiveStatus: string | null;
  releaseBundleDeliveryFinalizationArchiveDownloadStatus: string | null;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  deliveryNotes: string;
  releaseBundleDeliveryChannel: string;
  releaseBundleDeliveryTarget: string;
  releaseBundleDeliveryOperator: string;
  releaseBundleDeliveryNotes: string;
  onDownload: () => void;
  onArchive: () => void;
  onDownloadArchive: (archive: DemoFinalExternalReviewEvidencePackageArchive) => void;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onDeliveryNotesChange: (value: string) => void;
  onCreateDeliveryReceipt: () => void;
  onDownloadDeliveryReceipt: (receipt: DemoFinalExternalReviewEvidencePackageDeliveryReceipt) => void;
  onDownloadDeliveryFinalization: () => void;
  onArchiveDeliveryFinalization: () => void;
  onDownloadDeliveryFinalizationArchive: (
    archive: DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive
  ) => void;
  onDownloadDeliveryCertificate: () => void;
  onArchiveDeliveryCertificate: () => void;
  onDownloadDeliveryCertificateArchive: (
    archive: DemoFinalExternalReviewDeliveryCertificateArchive
  ) => void;
  onDownloadReleaseBundle: () => void;
  onArchiveReleaseBundle: () => void;
  onDownloadReleaseBundleArchive: (archive: DemoFinalExternalReviewReleaseBundleArchive) => void;
  onReleaseBundleDeliveryChannelChange: (value: string) => void;
  onReleaseBundleDeliveryTargetChange: (value: string) => void;
  onReleaseBundleDeliveryOperatorChange: (value: string) => void;
  onReleaseBundleDeliveryNotesChange: (value: string) => void;
  onCreateReleaseBundleDeliveryReceipt: () => void;
  onDownloadReleaseBundleDeliveryReceipt: (
    receipt: DemoFinalExternalReviewReleaseBundleDeliveryReceipt
  ) => void;
  onDownloadReleaseBundleDeliveryFinalization: () => void;
  onArchiveReleaseBundleDeliveryFinalization: () => void;
  onDownloadReleaseBundleDeliveryFinalizationArchive: (
    archive: DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive
  ) => void;
}

function FinalExternalReviewEvidencePackage({
  evidencePackage,
  archives,
  deliveryReceipts,
  deliveryFinalization,
  deliveryFinalizationArchives,
  deliveryCertificate,
  deliveryCertificateArchives,
  releaseBundle,
  releaseBundleArchives,
  releaseBundleDeliveryReceipts,
  releaseBundleDeliveryFinalization,
  releaseBundleDeliveryFinalizationArchives,
  error,
  archiveError,
  deliveryReceiptError,
  deliveryFinalizationError,
  deliveryFinalizationArchiveError,
  deliveryCertificateError,
  deliveryCertificateArchiveError,
  releaseBundleError,
  releaseBundleArchiveError,
  releaseBundleDeliveryReceiptError,
  releaseBundleDeliveryFinalizationError,
  releaseBundleDeliveryFinalizationArchiveError,
  downloadStatus,
  archiveStatus,
  archiveDownloadStatus,
  deliveryReceiptStatus,
  deliveryReceiptDownloadStatus,
  deliveryFinalizationDownloadStatus,
  deliveryFinalizationArchiveStatus,
  deliveryFinalizationArchiveDownloadStatus,
  deliveryCertificateDownloadStatus,
  deliveryCertificateArchiveStatus,
  deliveryCertificateArchiveDownloadStatus,
  releaseBundleDownloadStatus,
  releaseBundleArchiveStatus,
  releaseBundleArchiveDownloadStatus,
  releaseBundleDeliveryReceiptStatus,
  releaseBundleDeliveryReceiptDownloadStatus,
  releaseBundleDeliveryFinalizationDownloadStatus,
  releaseBundleDeliveryFinalizationArchiveStatus,
  releaseBundleDeliveryFinalizationArchiveDownloadStatus,
  deliveryChannel,
  deliveryTarget,
  operator,
  deliveryNotes,
  releaseBundleDeliveryChannel,
  releaseBundleDeliveryTarget,
  releaseBundleDeliveryOperator,
  releaseBundleDeliveryNotes,
  onDownload,
  onArchive,
  onDownloadArchive,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onOperatorChange,
  onDeliveryNotesChange,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceipt,
  onDownloadDeliveryFinalization,
  onArchiveDeliveryFinalization,
  onDownloadDeliveryFinalizationArchive,
  onDownloadDeliveryCertificate,
  onArchiveDeliveryCertificate,
  onDownloadDeliveryCertificateArchive,
  onDownloadReleaseBundle,
  onArchiveReleaseBundle,
  onDownloadReleaseBundleArchive,
  onReleaseBundleDeliveryChannelChange,
  onReleaseBundleDeliveryTargetChange,
  onReleaseBundleDeliveryOperatorChange,
  onReleaseBundleDeliveryNotesChange,
  onCreateReleaseBundleDeliveryReceipt,
  onDownloadReleaseBundleDeliveryReceipt,
  onDownloadReleaseBundleDeliveryFinalization,
  onArchiveReleaseBundleDeliveryFinalization,
  onDownloadReleaseBundleDeliveryFinalizationArchive
}: FinalExternalReviewEvidencePackageProps) {
  return (
    <div className="demo-session-handoff-checks">
      <div className="demo-session-archive-title-row">
        <h3>Final external-review evidence package</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownload()}
            aria-label="Download final external-review package"
            disabled={!evidencePackage}
          >
            <Download size={14} />
            Download final external-review package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onArchive()}
            aria-label="Archive final external-review evidence package"
            disabled={!evidencePackage}
          >
            <Archive size={14} />
            Archive final external-review package
          </button>
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          {archiveDownloadStatus ? <span className="copy-status">{archiveDownloadStatus}</span> : null}
        </div>
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Final external-review evidence package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}
      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Final external-review evidence package archives unavailable</strong>
          <span>{archiveError}</span>
        </div>
      ) : null}
      {deliveryReceiptError ? (
        <div className="adapter-api-error">
          <strong>Final external-review package delivery receipts unavailable</strong>
          <span>{deliveryReceiptError}</span>
        </div>
      ) : null}
      {deliveryFinalizationError ? (
        <div className="adapter-api-error">
          <strong>Final external-review package delivery finalization unavailable</strong>
          <span>{deliveryFinalizationError}</span>
        </div>
      ) : null}
      {deliveryFinalizationArchiveError ? (
        <div className="adapter-api-error">
          <strong>Final external-review package delivery finalization archives unavailable</strong>
          <span>{deliveryFinalizationArchiveError}</span>
        </div>
      ) : null}
      {evidencePackage ? (
        <>
          <div className="demo-session-summary">
            <div>
              <span>External review</span>
              <strong>{evidencePackage.readyForExternalReview ? 'Ready for external review' : statusLabel(evidencePackage.status)}</strong>
              <small>{evidencePackage.summary}</small>
            </div>
            <div>
              <span>Frozen closeout archive</span>
              <strong>{evidencePackage.closeoutArchiveId ?? 'No closeout archive'}</strong>
              <small>{evidencePackage.closeoutArchivedAt ? `Archived ${compactDateTime(evidencePackage.closeoutArchivedAt)}` : evidencePackage.nextAction}</small>
            </div>
            <div>
              <span>Completion proof</span>
              <strong>{evidencePackage.completionArchiveId ?? 'No completion archive'}</strong>
              <small>{evidencePackage.completionEvidenceDeliveryReceiptId ?? 'No completion delivery receipt'}</small>
            </div>
            <div>
              <span>Delivery target</span>
              <strong>{evidencePackage.deliveryTarget ?? 'No delivery target'}</strong>
              <small>{evidencePackage.deliveryChannel ?? 'No delivery channel'} - {evidencePackage.deliveryReceiptFreshness ?? 'No freshness'}</small>
            </div>
          </div>
          <ul>
            {evidencePackage.checks.map((check) => (
              <li key={check.name}>
                <div className="demo-webhook-delivery-main">
                  <strong>{check.name}</strong>
                  <span>{statusLabel(check.status)}</span>
                </div>
                <p>{check.summary}</p>
                <small>{check.nextAction}</small>
              </li>
            ))}
          </ul>
          <CompactList
            title="External review evidence"
            items={evidencePackage.evidenceNotes}
            emptyText="No final external-review evidence notes available."
          />
          <CompactList
            title="External review downloads"
            items={evidencePackage.downloadActions}
            emptyText="No final external-review download actions available."
          />
          <small>{evidencePackage.sideEffectContract}</small>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final external-review package delivery finalization</h3>
              <div className="demo-session-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadDeliveryFinalization()}
                  aria-label="Download final external-review package delivery finalization"
                  disabled={!deliveryFinalization}
                >
                  <Download size={14} />
                  Download final external-review package delivery finalization
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveDeliveryFinalization()}
                  aria-label="Archive final external-review package delivery finalization"
                  disabled={!deliveryFinalization}
                >
                  <Archive size={14} />
                  Archive final external-review package delivery finalization
                </button>
                {deliveryFinalizationDownloadStatus ? (
                  <span className="copy-status">{deliveryFinalizationDownloadStatus}</span>
                ) : null}
                {deliveryFinalizationArchiveStatus ? (
                  <span className="copy-status">{deliveryFinalizationArchiveStatus}</span>
                ) : null}
                {deliveryFinalizationArchiveDownloadStatus ? (
                  <span className="copy-status">{deliveryFinalizationArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {deliveryFinalization ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Finalized</span>
                    <strong>{deliveryFinalization.finalized ? 'Finalized' : statusLabel(deliveryFinalization.status)}</strong>
                    <small>{deliveryFinalization.summary}</small>
                  </div>
                  <div>
                    <span>Package archive</span>
                    <strong>{deliveryFinalization.latestArchiveId ?? 'No package archive'}</strong>
                    <small>{deliveryFinalization.latestCloseoutArchiveId ?? 'No closeout archive'}</small>
                  </div>
                  <div>
                    <span>Delivery receipt</span>
                    <strong>{deliveryFinalization.latestDeliveryReceiptId ?? 'No delivery receipt'}</strong>
                    <small>{deliveryFinalization.deliveryReceiptFreshnessSummary}</small>
                  </div>
                  <div>
                    <span>Delivery target</span>
                    <strong>{deliveryFinalization.latestDeliveryTarget ?? 'No delivery target'}</strong>
                    <small>{deliveryFinalization.latestDeliveryChannel ?? 'No delivery channel'} - {deliveryFinalization.deliveryReceiptFreshness}</small>
                  </div>
                </div>
                <ul>
                  {deliveryFinalization.checks.map((check) => (
                    <li key={check.name}>
                      <div className="demo-webhook-delivery-main">
                        <strong>{check.name}</strong>
                        <span>{statusLabel(check.status)}</span>
                      </div>
                      <p>{check.summary}</p>
                      <small>{check.nextAction}</small>
                    </li>
                  ))}
                </ul>
                <CompactList
                  title="Final external-review delivery finalization evidence"
                  items={deliveryFinalization.evidenceNotes}
                  emptyText="No final external-review package delivery finalization evidence available."
                />
                <CompactList
                  title="Final external-review delivery finalization downloads"
                  items={deliveryFinalization.downloadActions}
                  emptyText="No final external-review package delivery finalization downloads available."
                />
                <small>{deliveryFinalization.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final external-review package delivery finalization has not loaded yet.</p>
            )}
            <div className="demo-session-handoff-checks">
              <div className="demo-session-archive-title-row">
                <h3>Archived final external-review delivery finalizations</h3>
                <span>{deliveryFinalizationArchives.length} archives</span>
              </div>
              {deliveryFinalizationArchives.length > 0 ? (
                <ul>
                  {deliveryFinalizationArchives.map((archive) => (
                    <li key={archive.id}>
                      <div className="demo-webhook-delivery-main">
                        <strong>{archive.id}</strong>
                        <span>{archive.finalized ? 'Finalized' : statusLabel(archive.status)}</span>
                      </div>
                      <p>{archive.summary}</p>
                      <small>Package archive {archive.latestArchiveId ?? 'missing'}</small>
                      <small>Delivery receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                      <small>Completion receipt {archive.latestCompletionEvidenceDeliveryReceiptId ?? 'missing'}</small>
                      <small>Archived {compactDateTime(archive.archivedAt)}</small>
                      <div className="demo-session-archive-actions">
                        <button
                          className="secondary-button"
                          type="button"
                          onClick={() => onDownloadDeliveryFinalizationArchive(archive)}
                          aria-label={`Download final external-review package delivery finalization archive ${archive.id}`}
                        >
                          <Download size={14} />
                          Download final external-review package delivery finalization archive {archive.id}
                        </button>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-state">No final external-review package delivery finalization archives yet.</p>
              )}
            </div>
          </div>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final external-review delivery certificate</h3>
              <div className="demo-session-archive-actions">
                {deliveryCertificate ? (
                  <span className={`demo-readiness-status demo-readiness-status-${statusClass(deliveryCertificate.status)}`}>
                    {deliveryCertificate.certified ? 'Certified' : statusLabel(deliveryCertificate.status)}
                  </span>
                ) : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadDeliveryCertificate()}
                  aria-label="Download final external-review delivery certificate"
                  disabled={!deliveryCertificate}
                >
                  <Download size={14} />
                  Download final external-review delivery certificate
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveDeliveryCertificate()}
                  aria-label="Archive final external-review delivery certificate"
                  disabled={!deliveryCertificate}
                >
                  <Archive size={14} />
                  Archive final external-review delivery certificate
                </button>
                {deliveryCertificateDownloadStatus ? (
                  <span className="copy-status">{deliveryCertificateDownloadStatus}</span>
                ) : null}
                {deliveryCertificateArchiveStatus ? (
                  <span className="copy-status">{deliveryCertificateArchiveStatus}</span>
                ) : null}
                {deliveryCertificateArchiveDownloadStatus ? (
                  <span className="copy-status">{deliveryCertificateArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {deliveryCertificateError ? (
              <div className="adapter-api-error">
                <strong>Final external-review delivery certificate unavailable</strong>
                <span>{deliveryCertificateError}</span>
              </div>
            ) : null}
            {deliveryCertificateArchiveError ? (
              <div className="adapter-api-error">
                <strong>Final external-review delivery certificate archives unavailable</strong>
                <span>{deliveryCertificateArchiveError}</span>
              </div>
            ) : null}
            {deliveryCertificate ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Certificate</span>
                    <strong>
                      {deliveryCertificate.certified ? 'Certified' : statusLabel(deliveryCertificate.status)}
                    </strong>
                    <small>{deliveryCertificate.summary}</small>
                  </div>
                  <div>
                    <span>Delivery finalization archive</span>
                    <strong>
                      {deliveryCertificate.latestDeliveryFinalizationArchiveId ?? 'No finalization archive'}
                    </strong>
                    <small>
                      {deliveryCertificate.latestArchivedAt
                        ? `Archived ${compactDateTime(deliveryCertificate.latestArchivedAt)}`
                        : deliveryCertificate.nextAction}
                    </small>
                  </div>
                  <div>
                    <span>Delivery receipt</span>
                    <strong>{deliveryCertificate.latestDeliveryReceiptId ?? 'No delivery receipt'}</strong>
                    <small>
                      {deliveryCertificate.deliveryReceiptFresh
                        ? `Fresh - ${deliveryCertificate.deliveryReceiptFreshness}`
                        : deliveryCertificate.deliveryReceiptFreshness}
                    </small>
                  </div>
                  <div>
                    <span>Delivery target</span>
                    <strong>{deliveryCertificate.latestDeliveryTarget ?? 'No delivery target'}</strong>
                    <small>
                      {deliveryCertificate.latestDeliveryChannel ?? 'No delivery channel'}
                      {deliveryCertificate.latestDeliveredAt
                        ? ` - ${compactDateTime(deliveryCertificate.latestDeliveredAt)}`
                        : ''}
                    </small>
                  </div>
                </div>
                <ul>
                  {deliveryCertificate.checks.map((check) => (
                    <li key={check.name}>
                      <div className="demo-webhook-delivery-main">
                        <strong>{check.name}</strong>
                        <span>{statusLabel(check.status)}</span>
                      </div>
                      <p>{check.summary}</p>
                      <small>{check.nextAction}</small>
                    </li>
                  ))}
                </ul>
                <CompactList
                  title="Final external-review delivery certificate evidence"
                  items={deliveryCertificate.evidenceNotes}
                  emptyText="No final external-review delivery certificate evidence available."
                />
                <CompactList
                  title="Final external-review delivery certificate downloads"
                  items={deliveryCertificate.downloadActions}
                  emptyText="No final external-review delivery certificate downloads available."
                />
                <small>{deliveryCertificate.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final external-review delivery certificate has not loaded yet.</p>
            )}
            <div className="demo-session-handoff-checks">
              <div className="demo-session-archive-title-row">
                <h3>Archived final external-review delivery certificates</h3>
                <span>{deliveryCertificateArchives.length} archives</span>
              </div>
              {deliveryCertificateArchives.length > 0 ? (
                <ul>
                  {deliveryCertificateArchives.map((archive) => (
                    <li key={archive.id}>
                      <div className="demo-webhook-delivery-main">
                        <strong>{archive.id}</strong>
                        <span>{archive.certified ? 'Certified' : statusLabel(archive.status)}</span>
                      </div>
                      <p>{archive.summary}</p>
                      <small>
                        Delivery finalization archive {archive.latestDeliveryFinalizationArchiveId ?? 'missing'}
                      </small>
                      <small>Package archive {archive.latestPackageArchiveId ?? 'missing'}</small>
                      <small>Delivery receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                      <small>Archived {compactDateTime(archive.archivedAt)}</small>
                      <div className="demo-session-archive-actions">
                        <button
                          className="secondary-button"
                          type="button"
                          onClick={() => onDownloadDeliveryCertificateArchive(archive)}
                          aria-label={`Download final external-review delivery certificate archive ${archive.id}`}
                        >
                          <Download size={14} />
                          Download final external-review delivery certificate archive {archive.id}
                        </button>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-state">No final external-review delivery certificate archives yet.</p>
              )}
            </div>
            <div className="demo-session-handoff-checks">
              <div className="demo-session-archive-title-row">
                <h3>Final external-review release bundle</h3>
                <div className="demo-session-archive-actions">
                  {releaseBundle ? (
                    <span className={`demo-readiness-status demo-readiness-status-${statusClass(releaseBundle.status)}`}>
                      {releaseBundle.releaseReady ? 'Release ready' : statusLabel(releaseBundle.status)}
                    </span>
                  ) : null}
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => onDownloadReleaseBundle()}
                    aria-label="Download final external-review release bundle"
                    disabled={!releaseBundle}
                  >
                    <Download size={14} />
                    Download final external-review release bundle
                  </button>
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => onArchiveReleaseBundle()}
                    aria-label="Archive final external-review release bundle"
                    disabled={!releaseBundle || !releaseBundle.releaseReady}
                  >
                    <Archive size={14} />
                    Archive final external-review release bundle
                  </button>
                  {releaseBundleDownloadStatus ? (
                    <span className="copy-status">{releaseBundleDownloadStatus}</span>
                  ) : null}
                  {releaseBundleArchiveStatus ? (
                    <span className="copy-status">{releaseBundleArchiveStatus}</span>
                  ) : null}
                  {releaseBundleArchiveDownloadStatus ? (
                    <span className="copy-status">{releaseBundleArchiveDownloadStatus}</span>
                  ) : null}
                </div>
              </div>
              {releaseBundleError ? (
                <div className="adapter-api-error">
                  <strong>Final external-review release bundle unavailable</strong>
                  <span>{releaseBundleError}</span>
                </div>
              ) : null}
              {releaseBundleArchiveError ? (
                <div className="adapter-api-error">
                  <strong>Final external-review release bundle archives unavailable</strong>
                  <span>{releaseBundleArchiveError}</span>
                </div>
              ) : null}
              {releaseBundleDeliveryReceiptError ? (
                <div className="adapter-api-error">
                  <strong>Final external-review release bundle delivery receipts unavailable</strong>
                  <span>{releaseBundleDeliveryReceiptError}</span>
                </div>
              ) : null}
      {releaseBundleDeliveryFinalizationError ? (
        <div className="adapter-api-error">
          <strong>Final external-review release bundle delivery finalization unavailable</strong>
          <span>{releaseBundleDeliveryFinalizationError}</span>
        </div>
      ) : null}
      {releaseBundleDeliveryFinalizationArchiveError ? (
        <div className="adapter-api-error">
          <strong>Final external-review release bundle delivery finalization archives unavailable</strong>
          <span>{releaseBundleDeliveryFinalizationArchiveError}</span>
        </div>
      ) : null}
              {releaseBundle ? (
                <>
                  <div className="demo-session-summary">
                    <div>
                      <span>Release</span>
                      <strong>{releaseBundle.releaseReady ? 'Ready' : statusLabel(releaseBundle.status)}</strong>
                      <small>{releaseBundle.summary}</small>
                    </div>
                    <div>
                      <span>Certificate archive</span>
                      <strong>{releaseBundle.latestCertificateArchiveId ?? 'No certificate archive'}</strong>
                      <small>
                        {releaseBundle.latestCertificateArchivedAt
                          ? `Archived ${compactDateTime(releaseBundle.latestCertificateArchivedAt)}`
                          : releaseBundle.nextAction}
                      </small>
                    </div>
                    <div>
                      <span>Release delivery receipt</span>
                      <strong>{releaseBundle.latestDeliveryReceiptId ?? 'No delivery receipt'}</strong>
                      <small>
                        {releaseBundle.latestDeliveryTarget ?? 'No delivery target'}
                        {releaseBundle.latestDeliveryChannel ? ` via ${releaseBundle.latestDeliveryChannel}` : ''}
                      </small>
                    </div>
                    <div>
                      <span>Release Pull Request</span>
                      <strong>{releaseBundle.latestTaskId ?? 'No task'}</strong>
                      <small>{releaseBundle.latestPullRequestUrl ?? 'No Pull Request URL'}</small>
                    </div>
                  </div>
                  <CompactList
                    title="Final external-review release attachments"
                    items={releaseBundle.requiredAttachments}
                    emptyText="No final external-review release attachments available."
                  />
                  <ul>
                    {releaseBundle.releaseChecks.map((check) => (
                      <li key={check.name}>
                        <div className="demo-webhook-delivery-main">
                          <strong>{check.name}</strong>
                          <span>{statusLabel(check.status)}</span>
                        </div>
                        <p>{check.summary}</p>
                        <small>{check.nextAction}</small>
                      </li>
                    ))}
                  </ul>
                  <CompactList
                    title="Final external-review release evidence"
                    items={releaseBundle.evidenceNotes}
                    emptyText="No final external-review release evidence available."
                  />
                  <CompactList
                    title="Final external-review release downloads"
                    items={releaseBundle.downloadActions}
                    emptyText="No final external-review release downloads available."
                  />
                  <small>{releaseBundle.sideEffectContract}</small>
                </>
              ) : (
                <p className="empty-state">Final external-review release bundle has not loaded yet.</p>
              )}
              <div className="demo-session-handoff-checks">
                <div className="demo-session-archive-title-row">
                  <h3>Archived final external-review release bundles</h3>
                  <span>{releaseBundleArchives.length} archives</span>
                </div>
                {releaseBundleArchives.length > 0 ? (
                  <ul>
                    {releaseBundleArchives.map((archive) => (
                      <li key={archive.id}>
                        <div className="demo-webhook-delivery-main">
                          <strong>{archive.id}</strong>
                          <span>{archive.releaseReady ? 'Release ready' : statusLabel(archive.status)}</span>
                        </div>
                        <p>{archive.summary}</p>
                        <small>Certificate archive {archive.latestCertificateArchiveId ?? 'missing'}</small>
                        <small>
                          Delivery finalization archive {archive.latestDeliveryFinalizationArchiveId ?? 'missing'}
                        </small>
                        <small>Package archive {archive.latestPackageArchiveId ?? 'missing'}</small>
                        <small>Delivery receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                        <small>Archived {compactDateTime(archive.archivedAt)}</small>
                        <div className="demo-session-archive-actions">
                          <button
                            className="secondary-button"
                            type="button"
                            onClick={() => onDownloadReleaseBundleArchive(archive)}
                            aria-label={`Download final external-review release bundle archive ${archive.id}`}
                          >
                            <Download size={14} />
                            Download final external-review release bundle archive {archive.id}
                          </button>
                        </div>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="empty-state">No final external-review release bundle archives yet.</p>
                )}
              </div>
              <div className="demo-session-handoff-checks">
                <div className="demo-session-archive-title-row">
                  <h3>Final external-review release bundle delivery finalization</h3>
                  <div className="demo-session-archive-actions">
                    {releaseBundleDeliveryFinalization ? (
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(releaseBundleDeliveryFinalization.status)}`}>
                        {releaseBundleDeliveryFinalization.finalized
                          ? 'Finalized'
                          : statusLabel(releaseBundleDeliveryFinalization.status)}
                      </span>
                    ) : null}
                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() => onDownloadReleaseBundleDeliveryFinalization()}
                      aria-label="Download final external-review release bundle delivery finalization"
                      disabled={!releaseBundleDeliveryFinalization}
                    >
                      <Download size={14} />
                      Download final external-review release bundle delivery finalization
                    </button>
                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() => onArchiveReleaseBundleDeliveryFinalization()}
                      aria-label="Archive final external-review release bundle delivery finalization"
                      disabled={!releaseBundleDeliveryFinalization?.finalized}
                    >
                      <Archive size={14} />
                      Archive final external-review release bundle delivery finalization
                    </button>
                    {releaseBundleDeliveryFinalizationDownloadStatus ? (
                      <span className="copy-status">
                        {releaseBundleDeliveryFinalizationDownloadStatus}
                      </span>
                    ) : null}
                    {releaseBundleDeliveryFinalizationArchiveStatus ? (
                      <span className="copy-status">
                        {releaseBundleDeliveryFinalizationArchiveStatus}
                      </span>
                    ) : null}
                    {releaseBundleDeliveryFinalizationArchiveDownloadStatus ? (
                      <span className="copy-status">
                        {releaseBundleDeliveryFinalizationArchiveDownloadStatus}
                      </span>
                    ) : null}
                  </div>
                </div>
                {releaseBundleDeliveryFinalization ? (
                  <>
                    <div className="demo-session-summary">
                      <div>
                        <span>Finalized</span>
                        <strong>
                          {releaseBundleDeliveryFinalization.finalized
                            ? 'Finalized'
                            : statusLabel(releaseBundleDeliveryFinalization.status)}
                        </strong>
                        <small>{releaseBundleDeliveryFinalization.summary}</small>
                      </div>
                      <div>
                        <span>Release bundle archive</span>
                        <strong>{releaseBundleDeliveryFinalization.latestArchiveId ?? 'No release bundle archive'}</strong>
                        <small>{releaseBundleDeliveryFinalization.latestCertificateArchiveId ?? 'No certificate archive'}</small>
                      </div>
                      <div>
                        <span>Release bundle receipt</span>
                        <strong>{releaseBundleDeliveryFinalization.latestDeliveryReceiptId ?? 'No release bundle receipt'}</strong>
                        <small>{releaseBundleDeliveryFinalization.releaseBundleDeliveryReceiptFreshnessSummary}</small>
                      </div>
                      <div>
                        <span>Release bundle target</span>
                        <strong>{releaseBundleDeliveryFinalization.latestDeliveryTarget ?? 'No delivery target'}</strong>
                        <small>
                          {releaseBundleDeliveryFinalization.latestDeliveryChannel ?? 'No delivery channel'} - {releaseBundleDeliveryFinalization.releaseBundleDeliveryReceiptFreshness}
                        </small>
                      </div>
                    </div>
                    <ul>
                      {releaseBundleDeliveryFinalization.checks.map((check) => (
                        <li key={check.name}>
                          <div className="demo-webhook-delivery-main">
                            <strong>{check.name}</strong>
                            <span>{statusLabel(check.status)}</span>
                          </div>
                          <p>{check.summary}</p>
                          <small>{check.nextAction}</small>
                        </li>
                      ))}
                    </ul>
                    <CompactList
                      title="Final external-review release bundle delivery evidence"
                      items={releaseBundleDeliveryFinalization.evidenceNotes}
                      emptyText="No final external-review release bundle delivery evidence available."
                    />
                    <CompactList
                      title="Final external-review release bundle delivery downloads"
                      items={releaseBundleDeliveryFinalization.downloadActions}
                      emptyText="No final external-review release bundle delivery downloads available."
                    />
                    <small>{releaseBundleDeliveryFinalization.sideEffectContract}</small>
                    <div className="demo-webhook-delivery-trail">
                      <div className="section-heading">
                        <h4>Archived release bundle delivery finalizations</h4>
                        <span>{releaseBundleDeliveryFinalizationArchives.length} archives</span>
                      </div>
                      {releaseBundleDeliveryFinalizationArchives.length > 0 ? (
                        <ul>
                          {releaseBundleDeliveryFinalizationArchives.map((archive) => (
                            <li key={archive.id}>
                              <div className="demo-webhook-delivery-main">
                                <strong>{archive.id}</strong>
                                <span>{archive.finalized ? 'Finalized' : statusLabel(archive.status)}</span>
                              </div>
                              <p>{archive.summary}</p>
                              <small>Release bundle archive {archive.latestArchiveId ?? 'missing'}</small>
                              <small>Release bundle receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                              <small>Certificate archive {archive.latestCertificateArchiveId ?? 'missing'}</small>
                              <small>Archived {compactDateTime(archive.archivedAt)}</small>
                              <div className="demo-session-archive-actions">
                                <button
                                  className="secondary-button"
                                  type="button"
                                  onClick={() => onDownloadReleaseBundleDeliveryFinalizationArchive(archive)}
                                  aria-label={`Download final external-review release bundle delivery finalization archive ${archive.id}`}
                                >
                                  <Download size={14} />
                                  Download final external-review release bundle delivery finalization archive {archive.id}
                                </button>
                              </div>
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="empty-state">
                          No final external-review release bundle delivery finalization archives yet.
                        </p>
                      )}
                    </div>
                  </>
                ) : (
                  <p className="empty-state">
                    Final external-review release bundle delivery finalization has not loaded yet.
                  </p>
                )}
              </div>
              <div className="demo-evidence-receipt-form">
                <div className="demo-session-archive-title-row">
                  <h3>Record final external-review release bundle delivery receipt</h3>
                  {releaseBundleDeliveryReceiptStatus ? (
                    <span className="copy-status">{releaseBundleDeliveryReceiptStatus}</span>
                  ) : null}
                  {releaseBundleDeliveryReceiptDownloadStatus ? (
                    <span className="copy-status">{releaseBundleDeliveryReceiptDownloadStatus}</span>
                  ) : null}
                </div>
                <label>
                  Release bundle delivery channel
                  <select
                    value={releaseBundleDeliveryChannel}
                    onChange={(event) => onReleaseBundleDeliveryChannelChange(event.target.value)}
                  >
                    <option value="email">email</option>
                    <option value="slack">slack</option>
                    <option value="github-comment">github-comment</option>
                    <option value="manual">manual</option>
                  </select>
                </label>
                <label>
                  Release bundle delivery target
                  <input
                    value={releaseBundleDeliveryTarget}
                    onChange={(event) => onReleaseBundleDeliveryTargetChange(event.target.value)}
                    placeholder="reviewer@example.com"
                  />
                </label>
                <label>
                  Release bundle delivery operator
                  <input
                    value={releaseBundleDeliveryOperator}
                    onChange={(event) => onReleaseBundleDeliveryOperatorChange(event.target.value)}
                    placeholder="release-captain"
                  />
                </label>
                <label>
                  Release bundle delivery notes
                  <textarea
                    value={releaseBundleDeliveryNotes}
                    onChange={(event) => onReleaseBundleDeliveryNotesChange(event.target.value)}
                    placeholder="Sent the archived final external-review release bundle to the reviewer."
                  />
                </label>
                <button
                  className="primary-button"
                  type="button"
                  onClick={() => onCreateReleaseBundleDeliveryReceipt()}
                  disabled={releaseBundleArchives.length === 0}
                >
                  Record final external-review release bundle delivery receipt
                </button>
              </div>
              <div className="demo-session-handoff-checks">
                <div className="demo-session-archive-title-row">
                  <h3>Final external-review release bundle delivery receipts</h3>
                  <span>{releaseBundleDeliveryReceipts.length} receipts</span>
                </div>
                {releaseBundleDeliveryReceipts.length > 0 ? (
                  <ul>
                    {releaseBundleDeliveryReceipts.map((receipt) => (
                      <li key={receipt.id}>
                        <div className="demo-webhook-delivery-main">
                          <strong>{receipt.id}</strong>
                          <span>{statusLabel(receipt.status)}</span>
                        </div>
                        <p>{receipt.summary}</p>
                        <small>Release bundle archive {receipt.releaseBundleArchiveId}</small>
                        <small>Certificate archive {receipt.latestCertificateArchiveId ?? 'missing'}</small>
                        <small>Package archive {receipt.latestPackageArchiveId ?? 'missing'}</small>
                        <small>Package delivery receipt {receipt.latestPackageDeliveryReceiptId ?? 'missing'}</small>
                        <small>Target {receipt.deliveryTarget}</small>
                        <small>Channel {receipt.deliveryChannel}</small>
                        <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                        <div className="demo-session-archive-actions">
                          <button
                            className="secondary-button"
                            type="button"
                            onClick={() => onDownloadReleaseBundleDeliveryReceipt(receipt)}
                            aria-label={`Download final external-review release bundle delivery receipt ${receipt.id}`}
                          >
                            <Download size={14} />
                            Download final external-review release bundle delivery receipt {receipt.id}
                          </button>
                        </div>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="empty-state">
                    No final external-review release bundle delivery receipts recorded.
                  </p>
                )}
              </div>
            </div>
          </div>
          <div className="demo-evidence-receipt-form">
            <div className="demo-session-archive-title-row">
              <h3>Record final external-review package delivery receipt</h3>
              {deliveryReceiptStatus ? <span className="copy-status">{deliveryReceiptStatus}</span> : null}
              {deliveryReceiptDownloadStatus ? (
                <span className="copy-status">{deliveryReceiptDownloadStatus}</span>
              ) : null}
            </div>
            <label>
              Package delivery channel
              <select value={deliveryChannel} onChange={(event) => onDeliveryChannelChange(event.target.value)}>
                <option value="email">email</option>
                <option value="slack">slack</option>
                <option value="github-comment">github-comment</option>
                <option value="manual">manual</option>
              </select>
            </label>
            <label>
              Package delivery target
              <input
                value={deliveryTarget}
                onChange={(event) => onDeliveryTargetChange(event.target.value)}
                placeholder="reviewer@example.com"
              />
            </label>
            <label>
              Package delivery operator
              <input
                value={operator}
                onChange={(event) => onOperatorChange(event.target.value)}
                placeholder="local-operator"
              />
            </label>
            <label>
              Package delivery notes
              <textarea
                value={deliveryNotes}
                onChange={(event) => onDeliveryNotesChange(event.target.value)}
                placeholder="Sent the archived final external-review package to the reviewer."
              />
            </label>
            <button
              className="primary-button"
              type="button"
              onClick={() => onCreateDeliveryReceipt()}
              disabled={archives.length === 0}
            >
              Record final external-review package delivery receipt
            </button>
          </div>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final external-review package delivery receipts</h3>
              <span>{deliveryReceipts.length} receipts</span>
            </div>
            {deliveryReceipts.length > 0 ? (
              <ul>
                {deliveryReceipts.map((receipt) => (
                  <li key={receipt.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{receipt.id}</strong>
                      <span>{statusLabel(receipt.status)}</span>
                    </div>
                    <p>{receipt.summary}</p>
                    <small>Package archive {receipt.finalExternalReviewPackageArchiveId}</small>
                    <small>Closeout archive {receipt.closeoutArchiveId ?? 'missing'}</small>
                    <small>Completion receipt {receipt.completionEvidenceDeliveryReceiptId ?? 'missing'}</small>
                    <small>Target {receipt.deliveryTarget}</small>
                    <small>Channel {receipt.deliveryChannel}</small>
                    <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadDeliveryReceipt(receipt)}
                        aria-label={`Download final external-review package delivery receipt ${receipt.id}`}
                      >
                        <Download size={14} />
                        Download final external-review package delivery receipt {receipt.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final external-review package delivery receipts recorded.</p>
            )}
          </div>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Archived final external-review packages</h3>
              <span>{archives.length} archives</span>
            </div>
            {archives.length > 0 ? (
              <ul>
                {archives.map((archive) => (
                  <li key={archive.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{archive.id}</strong>
                      <span>{archive.readyForExternalReview ? 'Ready' : statusLabel(archive.status)}</span>
                    </div>
                    <p>{archive.summary}</p>
                    <small>Closeout archive {archive.closeoutArchiveId ?? 'missing'}</small>
                    <small>Completion archive {archive.completionArchiveId ?? 'missing'}</small>
                    <small>Completion receipt {archive.completionEvidenceDeliveryReceiptId ?? 'missing'}</small>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadArchive(archive)}
                        aria-label={`Download final external-review package ${archive.id}`}
                      >
                        <Download size={14} />
                        Download final external-review package {archive.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final external-review package archives yet.</p>
            )}
          </div>
        </>
      ) : (
        <p className="empty-state">Final external-review evidence package has not loaded yet.</p>
      )}
    </div>
  );
}

interface FinalAcceptanceSharePackageProps {
  sharePackage: DemoFinalAcceptanceSharePackage | null;
  archives: DemoFinalAcceptanceSharePackageArchive[];
  deliveryReceipts: DemoFinalAcceptanceShareDeliveryReceipt[];
  finalization: DemoFinalAcceptanceShareFinalization | null;
  completionEvidenceBundle: DemoFinalAcceptanceCompletionEvidenceBundle | null;
  completionEvidenceDeliveryFinalization: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization | null;
  completionCloseout: DemoFinalAcceptanceCompletionCloseout | null;
  completionEvidenceDeliveryReceipts: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[];
  completionArchives: DemoFinalAcceptanceCompletionArchive[];
  completionCloseoutArchives: DemoFinalAcceptanceCompletionCloseoutArchive[];
  error: string | null;
  archiveError: string | null;
  deliveryReceiptError: string | null;
  finalizationError: string | null;
  completionEvidenceBundleError: string | null;
  completionEvidenceDeliveryFinalizationError: string | null;
  completionCloseoutError: string | null;
  completionArchiveError: string | null;
  completionCloseoutArchiveError: string | null;
  completionEvidenceDeliveryReceiptError: string | null;
  copyStatus: string | null;
  downloadStatus: string | null;
  archiveStatus: string | null;
  archiveDownloadStatus: string | null;
  deliveryReceiptStatus: string | null;
  finalizationDownloadStatus: string | null;
  completionEvidenceBundleDownloadStatus: string | null;
  completionEvidenceDeliveryFinalizationDownloadStatus: string | null;
  completionCloseoutDownloadStatus: string | null;
  completionArchiveStatus: string | null;
  completionArchiveDownloadStatus: string | null;
  completionCloseoutArchiveStatus: string | null;
  completionCloseoutArchiveDownloadStatus: string | null;
  completionEvidenceDeliveryReceiptStatus: string | null;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  deliveryNotes: string;
  completionEvidenceDeliveryChannel: string;
  completionEvidenceDeliveryTarget: string;
  completionEvidenceOperator: string;
  completionEvidenceDeliveryNotes: string;
  onCopy: () => void;
  onDownload: () => void;
  onArchive: () => void;
  onDownloadArchive: (archive: DemoFinalAcceptanceSharePackageArchive) => void;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onDeliveryNotesChange: (value: string) => void;
  onCompletionEvidenceDeliveryChannelChange: (value: string) => void;
  onCompletionEvidenceDeliveryTargetChange: (value: string) => void;
  onCompletionEvidenceOperatorChange: (value: string) => void;
  onCompletionEvidenceDeliveryNotesChange: (value: string) => void;
  onCreateDeliveryReceipt: () => void;
  onDownloadDeliveryReceipt: (receipt: DemoFinalAcceptanceShareDeliveryReceipt) => void;
  onDownloadFinalization: () => void;
  onDownloadCompletionEvidenceBundle: () => void;
  onDownloadCompletionEvidenceDeliveryFinalization: () => void;
  onDownloadCompletionCloseout: () => void;
  onArchiveCompletion: () => void;
  onDownloadCompletionArchive: (archive: DemoFinalAcceptanceCompletionArchive) => void;
  onArchiveCompletionCloseout: () => void;
  onDownloadCompletionCloseoutArchive: (archive: DemoFinalAcceptanceCompletionCloseoutArchive) => void;
  onCreateCompletionEvidenceDeliveryReceipt: () => void;
  onDownloadCompletionEvidenceDeliveryReceipt: (
    receipt: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt
  ) => void;
}

function FinalAcceptanceSharePackage({
  sharePackage,
  archives,
  deliveryReceipts,
  finalization,
  completionEvidenceBundle,
  completionEvidenceDeliveryFinalization,
  completionCloseout,
  completionEvidenceDeliveryReceipts,
  completionArchives,
  completionCloseoutArchives,
  error,
  archiveError,
  deliveryReceiptError,
  finalizationError,
  completionEvidenceBundleError,
  completionEvidenceDeliveryFinalizationError,
  completionCloseoutError,
  completionArchiveError,
  completionCloseoutArchiveError,
  completionEvidenceDeliveryReceiptError,
  copyStatus,
  downloadStatus,
  archiveStatus,
  archiveDownloadStatus,
  deliveryReceiptStatus,
  finalizationDownloadStatus,
  completionEvidenceBundleDownloadStatus,
  completionEvidenceDeliveryFinalizationDownloadStatus,
  completionCloseoutDownloadStatus,
  completionArchiveStatus,
  completionArchiveDownloadStatus,
  completionCloseoutArchiveStatus,
  completionCloseoutArchiveDownloadStatus,
  completionEvidenceDeliveryReceiptStatus,
  deliveryChannel,
  deliveryTarget,
  operator,
  deliveryNotes,
  completionEvidenceDeliveryChannel,
  completionEvidenceDeliveryTarget,
  completionEvidenceOperator,
  completionEvidenceDeliveryNotes,
  onCopy,
  onDownload,
  onArchive,
  onDownloadArchive,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onOperatorChange,
  onDeliveryNotesChange,
  onCompletionEvidenceDeliveryChannelChange,
  onCompletionEvidenceDeliveryTargetChange,
  onCompletionEvidenceOperatorChange,
  onCompletionEvidenceDeliveryNotesChange,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceipt,
  onDownloadFinalization,
  onDownloadCompletionEvidenceBundle,
  onDownloadCompletionEvidenceDeliveryFinalization,
  onDownloadCompletionCloseout,
  onArchiveCompletion,
  onDownloadCompletionArchive,
  onArchiveCompletionCloseout,
  onDownloadCompletionCloseoutArchive,
  onCreateCompletionEvidenceDeliveryReceipt,
  onDownloadCompletionEvidenceDeliveryReceipt
}: FinalAcceptanceSharePackageProps) {
  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Final acceptance share package</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onCopy()}
            aria-label="Copy final acceptance share package"
            disabled={!sharePackage}
          >
            <Copy size={14} />
            Copy package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownload()}
            aria-label="Download final acceptance share package"
          >
            <Download size={14} />
            Download package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onArchive()}
            aria-label="Archive final acceptance share package"
            disabled={!sharePackage}
          >
            <Archive size={14} />
            Archive package
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          {archiveDownloadStatus ? <span className="copy-status">{archiveDownloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Final acceptance share package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {sharePackage ? (
        <>
          <div className="demo-session-summary">
            <div>
              <span>Send status</span>
              <strong>{sharePackage.sendReady ? 'Send-ready' : statusLabel(sharePackage.status)}</strong>
              <small>{sharePackage.summary}</small>
            </div>
            <div>
              <span>Message subject</span>
              <strong>{sharePackage.messageSubject}</strong>
              <small>Generated {compactDateTime(sharePackage.generatedAt)}</small>
            </div>
            <div>
              <span>Next action</span>
              <strong>{sharePackage.nextAction}</strong>
              <small>{sharePackage.latestPullRequestUrl ?? 'No Pull Request link'}</small>
            </div>
          </div>
          <div className="demo-session-lists compact-demo-session-lists">
            <CompactList
              title="Recommended recipients"
              items={sharePackage.recommendedRecipients}
              emptyText="No recommended recipients available."
            />
            <CompactList
              title="Required attachments"
              items={sharePackage.requiredAttachments}
              emptyText="No required attachments available."
            />
            <CompactList
              title="Pre-send checks"
              items={sharePackage.preSendChecks}
              emptyText="No pre-send checks available."
            />
          </div>
          <div className="demo-session-handoff-checks">
            <h3>Message template</h3>
            <p>{sharePackage.messageBody}</p>
            <small>{sharePackage.sideEffectContract}</small>
          </div>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Archived final acceptance packages</h3>
              <span>{archives.length} archives</span>
            </div>
            {archiveError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance package archives unavailable</strong>
                <span>{archiveError}</span>
              </div>
            ) : null}
            {archives.length > 0 ? (
              <ul>
                {archives.map((archive) => (
                  <li key={archive.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{archive.id}</strong>
                      <span>{archive.sendReady ? 'Send-ready' : statusLabel(archive.status)}</span>
                    </div>
                    <p>{archive.messageSubject}</p>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadArchive(archive)}
                        aria-label={`Download archived final acceptance package ${archive.id}`}
                      >
                        <Download size={14} />
                        Download archived package
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No archived final acceptance packages yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance delivery finalization</h3>
              <div className="demo-session-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadFinalization()}
                  aria-label="Download final acceptance finalization report"
                >
                  <Download size={14} />
                  Download final acceptance finalization report
                </button>
                {finalizationDownloadStatus ? <span className="copy-status">{finalizationDownloadStatus}</span> : null}
              </div>
            </div>
            {finalizationError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance finalization unavailable</strong>
                <span>{finalizationError}</span>
              </div>
            ) : null}
            {finalization ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Finalization</span>
                    <strong>{finalization.finalized ? 'Finalized' : statusLabel(finalization.status)}</strong>
                    <small>{finalization.summary}</small>
                  </div>
                  <div>
                    <span>Freshness</span>
                    <strong>{finalization.deliveryReceiptFreshness}</strong>
                    <small>{finalization.deliveryReceiptFreshnessSummary}</small>
                  </div>
                  <div>
                    <span>Latest receipt</span>
                    <strong>{finalization.latestDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{finalization.nextAction}</small>
                  </div>
                </div>
                <ul>
                  {finalization.checks.map((check) => (
                    <li key={check.name}>
                      <div className="demo-webhook-delivery-main">
                        <strong>{check.name}</strong>
                        <span>{statusLabel(check.status)}</span>
                      </div>
                      <p>{check.summary}</p>
                      <small>{check.nextAction}</small>
                    </li>
                  ))}
                </ul>
              </>
            ) : (
              <p className="empty-state">Final acceptance delivery finalization has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Record final acceptance delivery receipt</h3>
              {deliveryReceiptStatus ? <span className="copy-status">{deliveryReceiptStatus}</span> : null}
            </div>
            {deliveryReceiptError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance delivery receipts unavailable</strong>
                <span>{deliveryReceiptError}</span>
              </div>
            ) : null}
            <div className="manual-task-grid compact-manual-task-grid">
              <label>
                Delivery channel
                <select value={deliveryChannel} onChange={(event) => onDeliveryChannelChange(event.target.value)}>
                  <option value="email">email</option>
                  <option value="slack">slack</option>
                  <option value="github-comment">github-comment</option>
                  <option value="manual">manual</option>
                </select>
              </label>
              <label>
                Delivery target
                <input
                  value={deliveryTarget}
                  onChange={(event) => onDeliveryTargetChange(event.target.value)}
                  placeholder="reviewer@example.com"
                />
              </label>
              <label>
                Operator
                <input
                  value={operator}
                  onChange={(event) => onOperatorChange(event.target.value)}
                  placeholder="local-operator"
                />
              </label>
              <label>
                Delivery notes
                <textarea
                  value={deliveryNotes}
                  onChange={(event) => onDeliveryNotesChange(event.target.value)}
                  placeholder="Sent final acceptance share package to the reviewer."
                />
              </label>
            </div>
            <button className="primary-button" type="button" onClick={() => onCreateDeliveryReceipt()}>
              Record final acceptance delivery receipt
            </button>
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance delivery receipts</h3>
              <span>{deliveryReceipts.length} receipts</span>
            </div>
            {deliveryReceipts.length > 0 ? (
              <ul>
                {deliveryReceipts.map((receipt) => (
                  <li key={receipt.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{receipt.id}</strong>
                      <span>{receipt.deliveryChannel}</span>
                    </div>
                    <p>{receipt.messageSubject}</p>
                    <small>{receipt.deliveryTarget}</small>
                    <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadDeliveryReceipt(receipt)}
                        aria-label={`Download final acceptance delivery receipt ${receipt.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance delivery receipt {receipt.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance delivery receipts recorded.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Archived final acceptance completions</h3>
              <div className="demo-session-archive-actions">
                <span>{completionArchives.length} archives</span>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveCompletion()}
                  aria-label="Archive final acceptance completion"
                  disabled={!finalization?.finalized}
                >
                  <Archive size={14} />
                  Archive final acceptance completion
                </button>
                {completionArchiveStatus ? <span className="copy-status">{completionArchiveStatus}</span> : null}
                {completionArchiveDownloadStatus ? (
                  <span className="copy-status">{completionArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionArchiveError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion archives unavailable</strong>
                <span>{completionArchiveError}</span>
              </div>
            ) : null}
            {completionArchives.length > 0 ? (
              <ul>
                {completionArchives.map((archive) => (
                  <li key={archive.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{archive.id}</strong>
                      <span>{archive.finalized ? 'Finalized' : statusLabel(archive.status)}</span>
                    </div>
                    <p>{archive.deliveryReceiptFreshnessSummary}</p>
                    <small>Receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                    <small>Target {archive.latestDeliveryTarget ?? 'missing'}</small>
                    <small>Freshness {archive.deliveryReceiptFreshness}</small>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadCompletionArchive(archive)}
                        aria-label={`Download final acceptance completion ${archive.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance completion {archive.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance completion archives yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion evidence bundle</h3>
              <div className="demo-session-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionEvidenceBundle()}
                  aria-label="Download final acceptance completion evidence bundle"
                >
                  <Download size={14} />
                  Download completion bundle
                </button>
                {completionEvidenceBundleDownloadStatus ? (
                  <span className="copy-status">{completionEvidenceBundleDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceBundleError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion evidence bundle unavailable</strong>
                <span>{completionEvidenceBundleError}</span>
              </div>
            ) : null}
            {completionEvidenceBundle ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Share status</span>
                    <strong>{completionEvidenceBundle.readyToShare ? 'Share-ready' : statusLabel(completionEvidenceBundle.status)}</strong>
                    <small>{completionEvidenceBundle.summary}</small>
                  </div>
                  <div>
                    <span>Latest completion archive</span>
                    <strong>{completionEvidenceBundle.latestCompletionArchiveId ?? 'No archive'}</strong>
                    <small>{completionEvidenceBundle.completionArchiveCount} completion archives</small>
                  </div>
                  <div>
                    <span>Latest delivery</span>
                    <strong>{completionEvidenceBundle.latestDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{completionEvidenceBundle.latestDeliveryTarget ?? 'No delivery target'}</small>
                  </div>
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Completion evidence notes"
                    items={completionEvidenceBundle.evidenceNotes}
                    emptyText="No completion evidence notes available."
                  />
                  <CompactList
                    title="Completion download actions"
                    items={completionEvidenceBundle.downloadActions}
                    emptyText="No completion download actions available."
                  />
                </div>
                <small>{completionEvidenceBundle.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion evidence bundle has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion delivery finalization</h3>
              <div className="demo-session-archive-actions">
                {completionEvidenceDeliveryFinalization ? (
                  <span className={`demo-readiness-status demo-readiness-status-${statusClass(completionEvidenceDeliveryFinalization.status)}`}>
                    {statusLabel(completionEvidenceDeliveryFinalization.status)}
                  </span>
                ) : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionEvidenceDeliveryFinalization()}
                  aria-label="Download final acceptance completion delivery finalization report"
                >
                  <Download size={14} />
                  Download completion delivery finalization
                </button>
                {completionEvidenceDeliveryFinalizationDownloadStatus ? (
                  <span className="copy-status">{completionEvidenceDeliveryFinalizationDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceDeliveryFinalizationError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion delivery finalization unavailable</strong>
                <span>{completionEvidenceDeliveryFinalizationError}</span>
              </div>
            ) : null}
            {completionEvidenceDeliveryFinalization ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Finalization</span>
                    <strong>
                      {completionEvidenceDeliveryFinalization.finalized ? 'Finalized' : statusLabel(completionEvidenceDeliveryFinalization.status)}
                    </strong>
                    <small>{completionEvidenceDeliveryFinalization.summary}</small>
                  </div>
                  <div>
                    <span>Receipt freshness</span>
                    <strong>{completionEvidenceDeliveryFinalization.deliveryReceiptFreshness}</strong>
                    <small>{completionEvidenceDeliveryFinalization.deliveryReceiptFreshnessSummary}</small>
                  </div>
                  <div>
                    <span>Completion receipt</span>
                    <strong>
                      {completionEvidenceDeliveryFinalization.latestCompletionEvidenceDeliveryReceiptId ?? 'No receipt'}
                    </strong>
                    <small>
                      {completionEvidenceDeliveryFinalization.latestDeliveryTarget ?? 'No delivery target'}
                    </small>
                  </div>
                </div>
                <div className="demo-readiness-check-list compact-readiness-list">
                  {completionEvidenceDeliveryFinalization.checks.map((check) => (
                    <div key={check.name} className="demo-readiness-check">
                      <div>
                        <strong>{check.name}</strong>
                        <p>{check.summary}</p>
                        <p className="demo-readiness-check-action">{check.nextAction}</p>
                      </div>
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                        {statusLabel(check.status)}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Completion finalization evidence"
                    items={completionEvidenceDeliveryFinalization.evidenceNotes}
                    emptyText="No completion delivery finalization evidence available."
                  />
                  <CompactList
                    title="Completion finalization downloads"
                    items={completionEvidenceDeliveryFinalization.downloadActions}
                    emptyText="No completion delivery finalization downloads available."
                  />
                </div>
                <small>{completionEvidenceDeliveryFinalization.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion delivery finalization has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion closeout</h3>
              <div className="demo-session-archive-actions">
                {completionCloseout ? (
                  <span className={`demo-readiness-status demo-readiness-status-${statusClass(completionCloseout.status)}`}>
                    {statusLabel(completionCloseout.status)}
                  </span>
                ) : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionCloseout()}
                  aria-label="Download final acceptance completion closeout report"
                  disabled={!completionCloseout}
                >
                  <Download size={14} />
                  Download completion closeout
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveCompletionCloseout()}
                  aria-label="Archive final acceptance completion closeout"
                  disabled={!completionCloseout?.closed}
                >
                  <Archive size={14} />
                  Archive completion closeout
                </button>
                {completionCloseoutDownloadStatus ? (
                  <span className="copy-status">{completionCloseoutDownloadStatus}</span>
                ) : null}
                {completionCloseoutArchiveStatus ? (
                  <span className="copy-status">{completionCloseoutArchiveStatus}</span>
                ) : null}
                {completionCloseoutArchiveDownloadStatus ? (
                  <span className="copy-status">{completionCloseoutArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionCloseoutError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion closeout unavailable</strong>
                <span>{completionCloseoutError}</span>
              </div>
            ) : null}
            {completionCloseout ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Closeout</span>
                    <strong>{completionCloseout.closed ? 'Closed' : statusLabel(completionCloseout.status)}</strong>
                    <small>{completionCloseout.summary}</small>
                  </div>
                  <div>
                    <span>Completion receipt</span>
                    <strong>{completionCloseout.latestCompletionEvidenceDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{completionCloseout.deliveryReceiptFreshness}</small>
                  </div>
                  <div>
                    <span>Final review target</span>
                    <strong>{completionCloseout.latestDeliveryTarget ?? 'No delivery target'}</strong>
                    <small>{completionCloseout.nextAction}</small>
                  </div>
                </div>
                <div className="demo-readiness-check-list compact-readiness-list">
                  {completionCloseout.checks.map((check) => (
                    <div key={check.name} className="demo-readiness-check">
                      <div>
                        <strong>{check.name}</strong>
                        <p>{check.summary}</p>
                        <p className="demo-readiness-check-action">{check.nextAction}</p>
                      </div>
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                        {statusLabel(check.status)}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Closeout evidence"
                    items={completionCloseout.evidenceNotes}
                    emptyText="No closeout evidence available."
                  />
                  <CompactList
                    title="Closeout downloads"
                    items={completionCloseout.downloadActions}
                    emptyText="No closeout downloads available."
                  />
                </div>
                <small>{completionCloseout.sideEffectContract}</small>
                <div className="demo-session-handoff-checks">
                  <div className="demo-session-archive-title-row">
                    <h3>Archived final acceptance completion closeouts</h3>
                    <span>{completionCloseoutArchives.length} archives</span>
                  </div>
                  {completionCloseoutArchiveError ? (
                    <div className="adapter-api-error">
                      <strong>Final acceptance completion closeout archives unavailable</strong>
                      <span>{completionCloseoutArchiveError}</span>
                    </div>
                  ) : null}
                  {completionCloseoutArchives.length > 0 ? (
                    <ul>
                      {completionCloseoutArchives.map((archive) => (
                        <li key={archive.id}>
                          <div className="demo-webhook-delivery-main">
                            <strong>{archive.id}</strong>
                            <span>{archive.closed ? 'Closed' : statusLabel(archive.status)}</span>
                          </div>
                          <p>{archive.summary}</p>
                          <small>Completion archive {archive.latestCompletionArchiveId ?? 'missing'}</small>
                          <small>
                            Completion receipt {archive.latestCompletionEvidenceDeliveryReceiptId ?? 'missing'}
                          </small>
                          <small>Archived {compactDateTime(archive.archivedAt)}</small>
                          <div className="demo-session-archive-actions">
                            <button
                              className="secondary-button"
                              type="button"
                              onClick={() => onDownloadCompletionCloseoutArchive(archive)}
                              aria-label={`Download final acceptance completion closeout ${archive.id}`}
                            >
                              <Download size={14} />
                              Download final acceptance completion closeout {archive.id}
                            </button>
                          </div>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="empty-state">No final acceptance completion closeout archives yet.</p>
                  )}
                </div>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion closeout has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion evidence delivery receipts</h3>
              <div className="demo-session-archive-actions">
                <span>{completionEvidenceDeliveryReceipts.length} receipts</span>
                {completionEvidenceDeliveryReceiptStatus ? (
                  <span className="copy-status">{completionEvidenceDeliveryReceiptStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceDeliveryReceiptError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion evidence delivery receipts unavailable</strong>
                <span>{completionEvidenceDeliveryReceiptError}</span>
              </div>
            ) : null}
            <div className="demo-evidence-receipt-form">
              <h3>Record final acceptance completion evidence delivery receipt</h3>
              <label>
                Completion evidence delivery channel
                <select
                  value={completionEvidenceDeliveryChannel}
                  onChange={(event) => onCompletionEvidenceDeliveryChannelChange(event.target.value)}
                >
                  <option value="email">email</option>
                  <option value="slack">slack</option>
                  <option value="github-comment">github-comment</option>
                  <option value="manual">manual</option>
                </select>
              </label>
              <label>
                Completion evidence delivery target
                <input
                  value={completionEvidenceDeliveryTarget}
                  onChange={(event) => onCompletionEvidenceDeliveryTargetChange(event.target.value)}
                  placeholder="reviewer@example.com"
                />
              </label>
              <label>
                Completion evidence operator
                <input
                  value={completionEvidenceOperator}
                  onChange={(event) => onCompletionEvidenceOperatorChange(event.target.value)}
                  placeholder="local-operator"
                />
              </label>
              <label>
                Completion evidence delivery notes
                <textarea
                  value={completionEvidenceDeliveryNotes}
                  onChange={(event) => onCompletionEvidenceDeliveryNotesChange(event.target.value)}
                  placeholder="Sent final completion evidence bundle to the reviewer."
                />
              </label>
              <button
                className="primary-button"
                type="button"
                onClick={() => onCreateCompletionEvidenceDeliveryReceipt()}
                disabled={!completionEvidenceBundle?.readyToShare}
              >
                Record final acceptance completion evidence delivery receipt
              </button>
            </div>
            {completionEvidenceDeliveryReceipts.length > 0 ? (
              <ul>
                {completionEvidenceDeliveryReceipts.map((receipt) => (
                  <li key={receipt.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{receipt.id}</strong>
                      <span>{statusLabel(receipt.status)}</span>
                    </div>
                    <p>{receipt.summary}</p>
                    <small>Completion archive {receipt.latestCompletionArchiveId}</small>
                    <small>Target {receipt.deliveryTarget}</small>
                    <small>Channel {receipt.deliveryChannel}</small>
                    <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadCompletionEvidenceDeliveryReceipt(receipt)}
                        aria-label={`Download final acceptance completion evidence delivery receipt ${receipt.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance completion evidence delivery receipt {receipt.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance completion evidence delivery receipts recorded.</p>
            )}
          </div>
        </>
      ) : (
        <p className="empty-state">Final acceptance share package has not loaded yet.</p>
      )}
    </div>
  );
}

interface CompactListProps {
  title: string;
  items: string[];
  emptyText: string;
}

function CompactList({ title, items, emptyText }: CompactListProps) {
  return (
    <div>
      <h3>{title}</h3>
      {items.length > 0 ? (
        <ul>
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p>{emptyText}</p>
      )}
    </div>
  );
}

function formatSharePackageClipboard(sharePackage: DemoFinalAcceptanceSharePackage) {
  return [
    `Subject: ${sharePackage.messageSubject}`,
    '',
    sharePackage.messageBody,
    '',
    'Required attachments:',
    ...sharePackage.requiredAttachments.map((attachment) => `- ${attachment}`),
    '',
    'Pre-send checks:',
    ...sharePackage.preSendChecks.map((check) => `- ${check}`)
  ].join('\n');
}

interface AcceptanceStatProps {
  label: string;
  value: string;
  detail: string;
}

function AcceptanceStat({ label, value, detail }: AcceptanceStatProps) {
  return (
    <div className="demo-evidence-stat">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </div>
  );
}

interface CertificateEvidenceProps {
  title: string;
  status: DemoReadinessStatus;
  archived: boolean;
  certified: boolean;
  archiveId: string | null;
  closeoutArchiveId: string | null;
  evidenceArchiveId: string | null;
  deliveryReceiptId: string | null;
}

function CertificateEvidence({
  title,
  status,
  archived,
  certified,
  archiveId,
  closeoutArchiveId,
  evidenceArchiveId,
  deliveryReceiptId
}: CertificateEvidenceProps) {
  return (
    <div>
      <span>{title}</span>
      <strong>{certified ? 'Certified archive' : statusLabel(status)}</strong>
      <small>{archived ? 'Archive recorded' : 'Archive missing'}</small>
      <small>{archiveId ?? 'No certificate archive'}</small>
      <small>{closeoutArchiveId ?? 'No linked closeout archive'}</small>
      <small>{evidenceArchiveId ?? 'No linked evidence archive'}</small>
      <small>{deliveryReceiptId ?? 'No linked delivery receipt'}</small>
    </div>
  );
}

function statusLabel(status: DemoReadinessStatus) {
  switch (status) {
    case 'READY':
      return 'Ready';
    case 'NEEDS_ATTENTION':
      return 'Needs attention';
    case 'BLOCKED':
      return 'Blocked';
  }
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}
