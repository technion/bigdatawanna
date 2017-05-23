#!/usr/bin/env ruby

prev_key = nil
key_total = 0

ARGF.each do |line|

   # remove any newline
   line = line.chomp

   # split key and value on tab character
   (key, value) = line.split(/\t/)

   # check for new key
   if prev_key && key != prev_key && key_total > 0

      puts prev_key + "\t" + key_total.to_s

      # reset key total for new key
      prev_key = key
      key_total = 0

   elsif ! prev_key
      prev_key = key

   end

   # add to count for this current key
   key_total += 1 if value.match(/vulnerable/)

end
