private void setValueOnChange(DatabaseReference databaseReference, TextView textView) {
        // Look for the data change(read)
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
}

DatabaseReference.setValue(//Enter the value to be written in firebase database);
