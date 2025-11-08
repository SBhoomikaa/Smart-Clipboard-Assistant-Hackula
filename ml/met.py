from tflite_support.metadata_writers import bert_nl_classifier
from tflite_support.metadata_writers import metadata_info
import os
import inspect

INPUT_TFLITE_MODEL = "C:\\Users\\008bh\\Downloads\\distilbert_model_clean.tflite"
LABELS_FILE = "C:\\Users\\008bh\\Downloads\\labels (9).txt"
VOCAB_FILE = "C:\\Users\\008bh\\Downloads\\vocab (6).txt"
OUTPUT_TFLITE_MODEL = "C:\\Users\\008bh\\Downloads\\distilbert_with_metaadata.tflite"

# Load the model
print(f" Loading model from: {INPUT_TFLITE_MODEL}")
with open(INPUT_TFLITE_MODEL, "rb") as f:
    model_buffer = bytearray(f.read())
print(f" Model loaded ({len(model_buffer) / 1024 / 1024:.2f} MB)")

# Create tokenizer metadata
tokenizer_md = metadata_info.BertTokenizerMd(
    vocab_file_path=VOCAB_FILE
)
print(f"Tokenizer configured")
print(f"- Vocab file: {os.path.basename(VOCAB_FILE)}")

# Create metadata writer for BERT NL Classifier
# FIX: Explicitly specify tensor names to match your model

writer = bert_nl_classifier.MetadataWriter.create_for_inference(
    model_buffer=model_buffer,
    tokenizer_md=tokenizer_md,
    label_file_paths=[LABELS_FILE],
    ids_name="ids",              
    mask_name="mask",            
    segment_name="segment_ids"   
)
print(f"Metadata writer created")
print(f"-Label file: {os.path.basename(LABELS_FILE)}")
print(f"-Tensor names: ids, mask, segment_ids")

# Populate metadata into model
model_with_metadata = writer.populate()

# Save the model
with open(OUTPUT_TFLITE_MODEL, "wb") as f:
    f.write(model_with_metadata)

print(f"Model saved: {OUTPUT_TFLITE_MODEL}")
print("="*80)
print("METADATA EMBEDDING COMPLETE!")
print("="*80)

