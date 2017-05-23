#!/usr/bin/env ruby

ARGF.each do |line|
  line = line.chomp
  #Example line:
  #| ad.net.au|DARAPC03                      | safe       | |KB4013429|KB4015217|KB4015217
  next unless m = line.match(/(?<domain>[.a-zA-Z]+).*(?<state>vulnerable|safe)/)
  puts m[:domain] + "\t" + m[:state]
end

