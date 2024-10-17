#!/bin/bash
sum_pages=0

# Loop through all PDF files in the current directory
for file in *.pdf; do
    # Get the number of pages in the current PDF file
    cur_pages=$(pdfinfo "$file" | awk '/Pages/{print $2}')
    echo "PDF: $file has $cur_pages pages."
    
    # Add to the total page count
    sum_pages=$(( sum_pages + cur_pages ))
done

echo "Total number of pages in all PDF files: $sum_pages"
