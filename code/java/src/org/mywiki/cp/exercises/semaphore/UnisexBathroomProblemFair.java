package org.mywiki.cp.exercises.semaphore;

//#define SHARED 1
//#define MAXNUMBEROFMEN 5	/* maximum number of men */
//#define MAXNUMBEROFWOMEN 5	/* maximum number of women */
//#define MAXVISITS 10		/* maximum number of visits to bath each person makes */
//#define MAXC 4		/* maximum number of men/women allowed to enter */
//
//int numMen, numWomen, numVisits;
//
//sem_t e;			/* controls entry to critical sections */
//sem_t m;			/* used to delay men */
//sem_t w;			/* used to delay women */
//
//int nm = 0;			/* NumberOfMen */
//int nw = 0;			/* NumberOfWomen */
///* Invariant: (nm == 0 V nw == 0) */
//int dm = 0;			/* Number of delayed men */
//int dw = 0;			/* Number of delayed women */
//int cm = 0;			/* Count of men visited the bath */
//int cw = 0;			/* Count of women visited the bath */
//int C = 0;			/* Maximum number of men/women allowed to enter */
//void *Man (void *);		/* Man thread function */
//void *Woman (void *);		/* Woman thread function */
//pthread_mutex_t barrier;  /* mutex lock for the barrier */
//pthread_cond_t go;        /* condition variable for leaving */
//int numProc;
//int numArrived = 0;       /* number of arrived threads */
//
///* a reusable counter barrier */
//void Barrier() {
//	pthread_mutex_lock(&barrier);
//	numArrived++;
//	if (numArrived == numProc) {
//		numArrived = 0;
//		pthread_cond_broadcast(&go);
//	} else
//		pthread_cond_wait(&go, &barrier);
//	pthread_mutex_unlock(&barrier);
//}
//
///* initialize semahores, create threads */
//int
//main (int argc, char *argv[])
//{
//	int i;
//      long l;
//	pthread_t tm[MAXNUMBEROFMEN], tw[MAXNUMBEROFMEN];
//	/* initialize mutex and condition variable */
//	pthread_mutex_init (&barrier, NULL);
//	pthread_cond_init (&go, NULL);
//
//	/* initialize semaphores */
//	sem_init (&e, SHARED, 1);
//	sem_init (&m, SHARED, 0);
//	sem_init (&w, SHARED, 0);
//
//	while (numMen <= 0 || numMen > MAXNUMBEROFMEN)
//	{
//		printf ("Enter the number men: (max %d) ", MAXNUMBEROFMEN);
//		scanf ("%d", &numMen);
//	}
//	while (numWomen <= 0 || numWomen > MAXNUMBEROFWOMEN)
//	{
//		printf ("Enter the number women: (max %d) ", MAXNUMBEROFWOMEN);
//		scanf ("%d", &numWomen);
//	}
//	while (numVisits <= 0 || numVisits > MAXVISITS)
//	{
//		printf ("Enter the number visits: (max %d) ", MAXVISITS);
//		scanf ("%d", &numVisits);
//	}
//	while (C <= 0 || C > MAXC)
//	{
//		printf ("Enter the number of men/women allowed to enter: (max %d) ", MAXC);
//		scanf ("%d", &C);
//	}
//	cw = cm = C;
//	/* create the threads and exit */
//	numProc = numMen + numWomen;
//	for (l = numMen; l > 0; l--)
//		pthread_create (&tm[l-1], NULL, Man, (void *) l);
//	for (l = numWomen; l > 0; l--)
//		pthread_create (&tw[l-1], NULL, Woman, (void *) l);
//	for (i = numMen; i > 0; i--)
//		pthread_join (tm[i-1], NULL);
//	for (i = numWomen; i > 0; i--)
//		pthread_join (tw[i-1], NULL);
//	pthread_exit (NULL);
//}
//
//void *
//Man (void *arg)
//{
//	int j;
//	long i = (long) arg;
//	Barrier();
//	for (j = 0; j < numVisits; j++)
//	{
//		sem_wait (&e);
//		if (nw > 0 || cm == 0)
//		{
//			dm++;
//			sem_post (&e);
//			sem_wait (&m);
//		}
//		if (dw > 0) cm--;
//		nm++;
//		if (dm > 0 && cm > 0)
//		{
//			dm--;
//			sem_post (&m);
//		}
//		else
//			sem_post (&e);
//		printf ("   --> man %d enters the bathroom. visits: %d\n", i, j + 1);
//		sleep (rand () % 2);	// take a bath
//		sem_wait (&e);
//		nm--;
//		if (dm > 0 && cm > 0)
//		{
//			dm--;
//			sem_post (&m);
//		}
//		else if (nm == 0 && dw > 0)
//		{
//			dw--;
//			cw = C;
//			sem_post (&w);
//		}
//		else if (nm == 0) {
//			cw = C;
//			sem_post (&e);
//		}
//		else
//			sem_post (&e);
//		sleep (rand () % 3);	// work
//	}
//}
//
//void *
//Woman (void *arg)
//{
//	int j;
//	long i = (long) arg;
//	Barrier();
//	for (j = 0; j < numVisits; j++)
//	{
//		sem_wait (&e);
//		if (nm > 0 || cw == 0)
//		{
//			dw++;
//			sem_post (&e);
//			sem_wait (&w);
//		}
//		if (dm > 0) cw--;
//		nw++;
//		if (dw > 0 && cw > 0)
//		{
//			dw--;
//			sem_post (&w);
//		}
//		else
//			sem_post (&e);
//		printf ("--> woman %d enters the bathroom. visits: %d\n", i, j + 1);
//		sleep (rand () % 2);	// take a bath
//		sem_wait (&e);
//		nw--;
//		if (dw > 0 && cw > 0)
//		{
//			dw--;
//			sem_post (&w);
//		}
//		else if (nw == 0 && dm > 0)
//		{
//			dm--;
//			cm = C;
//			sem_post (&m);
//		}
//		else if (nw == 0) {
//			cm = C;
//			sem_post (&e);
//		}
//		else
//			sem_post (&e);
//		sleep (rand () % 3);	// work
//	}
//}

public class UnisexBathroomProblemFair {

}
