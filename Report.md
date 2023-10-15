Assignment 1
------------

# Team Members
Nico Keller
Teo Field-Marsham

# GitHub link to your (forked) repository

...

# Task 1

1. Indicate the time necessary for the SimpleCrawler to work.

Ans: Our SimpleCrawler took 59seconds in the last testing. I varied between
59 seconds and 1m 2seconds overall.



# Task 2

1. Is the flipped index smaller or larger than the initial index? What does this depend on?

Ans: The flipped index was actually larger than the initial index. This is due to the fact that,
there are more words than links to the webpage. Every webpage contained three words, and even though
some words got repeated, there are still more words than websites.

# Task 3

1. Explain your design choices for the API design.

Ans: We designed the API based off of the specifications given to us for administrative control of the program and general maintenance. 
We implemented the four key admin features: (/admin/crawl, /admin/regenerate, /admin/deleteUrl and /admin/updateUrl).  
These features clearly describe their purpose, ensure the secure execution of potentially impactful operations (the admin prefix for each command means someone is unlikely to accidentally use them), and allow admins to 
easily maintain the system's health and accuracy.

ADDITIONAL INFO FOR TASK 3: The code runs either when connecting via "127.0.0.1" or when connecting to "localhost:80".


# Task 4

1.  Indicate the time necessary for the MultithreadCrawler to work.

Ans: Multithreaded takes 8 seconds on Teo's PC.

SimpleCrawler takes 44 seconds on Teo's PC.

3. Indicate the ratio of the time for the SimpleCrawler divided by the time for the MultithreadedCrawler to get the increase in speed.

Ans: 44/8 = 5.5


